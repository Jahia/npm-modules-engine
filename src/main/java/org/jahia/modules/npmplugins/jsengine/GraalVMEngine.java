package org.jahia.modules.npmplugins.jsengine;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.helpers.OSGiServiceInjector;
import org.jahia.modules.npmplugins.registrars.Registrar;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRTemplate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.throwing.ThrowingSupplier;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base JS engine based on GraalVM
 */
@Component(service = GraalVMEngine.class, immediate = true)
public class GraalVMEngine {
    private static final Logger logger = LoggerFactory.getLogger(GraalVMEngine.class);

    public static final String JS = "js";

    private Engine sharedEngine;

    private List<JSGlobalVariableFactory> globals = new ArrayList<>();

    private GenericObjectPool<ContextProvider> pool;
    private ThreadLocal<ContextProvider> currentContext = new ThreadLocal<>();

    private LinkedHashMap<Bundle, Source> initScripts = new LinkedHashMap<>();
    private AtomicInteger version = new AtomicInteger(0);

    private Collection<Registrar> registrars = new ArrayList<>();

    @Reference(service = JSGlobalVariableFactory.class, policy = ReferencePolicy.STATIC, cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
    public void bindVariable(JSGlobalVariableFactory global) {
        globals.add(global);
    }

    public void unbindVariable(JSGlobalVariableFactory global) {
        globals.remove(global);
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
    public void addRegistrar(Registrar registrar) {
        registrars.add(registrar);
    }

    public void removeRegistrar(Registrar registrar) {
        registrars.remove(registrar);
    }

    public void addInitScript(Bundle bundle, String script) throws IOException {
        initScripts.put(bundle, getGraalSource(bundle, script));
        version.incrementAndGet();
        doWithContext(contextProvider -> {
            for (Registrar registrar : registrars) {
                registrar.register(contextProvider.getRegistry(), bundle, this);
            }
        });
    }

    public void removeInitScript(Bundle bundle) {
        doWithContext(contextProvider -> {
            for (Registrar registrar : registrars) {
                registrar.unregister(contextProvider.getRegistry(), bundle);
            }
        });

        if (initScripts.remove(bundle) != null) {
            version.incrementAndGet();
        }
    }

    @Activate
    public void activate(BundleContext bundleContext, Map<String, ?> props) {
        try {
            initScripts.put(bundleContext.getBundle(), getGraalSource(bundleContext.getBundle(), "META-INF/js/main.js"));
        } catch (IOException e) {
            logger.error("Cannot execute main init script", e);
        }

        Engine.Builder builder = Engine.newBuilder();
        boolean experimental = props.containsKey("experimental") && Boolean.parseBoolean(props.get("experimental").toString());
        builder.allowExperimentalOptions(experimental);
        for (Map.Entry<String, ?> entry : props.entrySet()) {
            if (entry.getKey().startsWith("polyglot.")) {
                String opt = StringUtils.substringAfter(entry.getKey(),"polyglot.");
                builder.option(opt, entry.getValue().toString());
            }
        }

        sharedEngine = builder.build();

        GenericObjectPoolConfig<ContextProvider> config = new GenericObjectPoolConfig<>();
        config.setTestOnBorrow(true);
        pool = new GenericObjectPool<>(new ContextPoolFactory(), config);
    }

    @Deactivate
    public void deactivate() {
        pool.close();
        sharedEngine.close();
    }

    public <T> T doWithContext(Function<ContextProvider, T> callback) {
        ContextProvider cx = currentContext.get();
        if (cx != null) {
            return callback.apply(cx);
        } else {
            try {
                cx = pool.borrowObject();
                currentContext.set(cx);
            } catch (Exception e) {
                throw new GraalVMException("Unable to borrow context from pool: " + e.getMessage(), e);
            }
            try {
                return callback.apply(cx);
            } finally {
                currentContext.remove();
                pool.returnObject(cx);
            }
        }
    }

    public void doWithContext(Consumer<ContextProvider> callback) {
        doWithContext(contextProvider -> {
            callback.accept(contextProvider);
            return null;
        });
    }

    private Source getGraalSource(Bundle bundle, String script) throws IOException {
        String resource = loadResource(bundle, script);
        if (resource == null) {
            throw new IOException("Cannot get resource "+ bundle.getSymbolicName() +" / " + script);
        }
        return Source.newBuilder(JS, resource, bundle.getSymbolicName() + "/" + script).build();
    }

    private static String getSourceFile(Bundle bundle, String path) throws RepositoryException {
        return JCRTemplate.getInstance().doExecuteWithSystemSession(session -> {
            JahiaTemplatesPackage pkg = BundleUtils.getModule(bundle);
            String sourcePath = "/modules/" + pkg.getIdWithVersion() + "/sources";
            if (!session.itemExists(sourcePath)) {
                return null;
            }
            JCRNodeWrapper sources = session.getNode(sourcePath);
            if (sources.hasNode(path)) {
                try {
                    return IOUtils.toString(sources.getNode(path).getFileContent().downloadFile());
                } catch (IOException e) {
                    throw new RepositoryException(e);
                }
            }
            return null;
        });
    }

    public static String loadResource(Bundle bundle, String path) {
        try {
            return Optional
                    .ofNullable(getSourceFile(bundle, path))
                    .orElseGet(ThrowingSupplier.unchecked(() -> IOUtils.toString(bundle.getResource(path))));
        } catch (Exception e) {
            logger.error("Cannot get resource", e);
        }
        return null;
    }


    class ContextPoolFactory extends BasePooledObjectFactory<ContextProvider> {
        @Override
        public ContextProvider create() throws Exception {
            logger.info("ContextPoolFactory.create");
            Context context = Context.newBuilder(JS)
                    .allowHostClassLookup(s -> true)
                    .allowHostAccess(HostAccess.ALL)
                    .allowPolyglotAccess(PolyglotAccess.ALL)
                    .allowIO(true)
                    .engine(sharedEngine).build();

            ContextProvider contextProvider = new ContextProvider(context, version.get());

            Map<String, Object> helpers = new HashMap<>();
            for (JSGlobalVariableFactory global : globals) {
                Map<String,Object> instances = global.getHelperInstances(contextProvider);
                for (Map.Entry<String, Object> entry : instances.entrySet()) {
                    OSGiServiceInjector.handleMethodInjection(entry.getValue());
                    helpers.put(entry.getKey(), entry.getValue());
                }
            }
            contextProvider.getHelpers().putAll(helpers);
            context.getBindings(JS).putMember("jahiaHelpers", ProxyObject.fromMap(helpers));

            // Initialize context with available JS
            for (Map.Entry<Bundle, Source> entry : initScripts.entrySet()) {
                try {
                    context.getBindings(JS).putMember("bundle", entry.getKey());
                    context.eval(entry.getValue());
                    contextProvider.getRegisteredBundles().add(entry.getKey());
                    context.getBindings(JS).removeMember("bundle");
                } catch (Exception e) {
                    logger.error("Cannot execute init script {}", entry.getValue(), e);
                }
            }
            return contextProvider;
        }

        @Override
        public PooledObject<ContextProvider> wrap(ContextProvider context) {
            return new DefaultPooledObject<>(context);
        }

        @Override
        public boolean validateObject(PooledObject<ContextProvider> p) {
            return version.get() == p.getObject().getVersion();
        }

        @Override
        public void destroyObject(PooledObject<ContextProvider> p) throws Exception {
            logger.info("ContextPoolFactory.destroyObject");
            p.getObject().close();
        }
    }
}
