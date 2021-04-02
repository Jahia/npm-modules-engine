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
import pl.touk.throwing.ThrowingSupplier;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base JS engine based on GraalVM
 */
@Component(service = GraalVMEngine.class, immediate = true)
public class GraalVMEngine {
    public static final String JS = "js";

    private Engine sharedEngine;

    private List<JSGlobalVariableFactory> globals = new ArrayList<>();

    private GenericObjectPool<ContextProvider> pool;
    private ThreadLocal<ContextProvider> currentContext = new ThreadLocal<>();

    private LinkedHashMap<Bundle, Source> initScripts = new LinkedHashMap<>();

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

        initScripts.remove(bundle);
    }

    @Activate
    public void activate(BundleContext bundleContext, Map<String, ?> props) {
        try {
            initScripts.put(bundleContext.getBundle(), getGraalSource(bundleContext.getBundle(), "META-INF/js/main.js"));
        } catch (IOException e) {
            e.printStackTrace();
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

//    /**
//     * Execute JS code from URL
//     */
//    public Value executeJs(URL code) throws IOException {
//        return executeJs(code, null);
//    }
//
//    /**
//     * Execute JS code from URL with additional bindings
//     */
//    public Value executeJs(URL code, Map<String, Object> bindings) throws IOException {
//        ContextProvider cx = getContextProvider();
//        return cx.doWithinLock(() -> {
//            Map<String, Object> oldBindings = new HashMap<>();
//            Value jsBindings = cx.getContext().getBindings(JS);
//            try {
//                if (bindings != null) {
//                    bindings.forEach((key, value) -> {
//                        oldBindings.put(key, jsBindings.getMember(key));
//                        jsBindings.putMember(key, value);
//                    });
//                }
//                Source source = Source.newBuilder(JS, code)
//                        .cached(false)
//                        .uri(new URI(code.toExternalForm()))
//                        .build();
//                return cx.getContext().eval(source);
//            } catch (URISyntaxException e) {
//                throw new IOException(e);
//            } finally {
//                oldBindings.forEach(jsBindings::putMember);
//            }
//        });
//    }

    public <T> T doWithContext(Function<ContextProvider, T> callback) {
        ContextProvider cx = currentContext.get();
        if (cx != null) {
            return callback.apply(cx);
        } else {
            try {
                cx = pool.borrowObject();
                currentContext.set(cx);
            } catch (Exception e) {
                throw new RuntimeException("Unable to borrow context from pool" + e.toString(), e);
            }
            try {
                return callback.apply(cx);
            } finally {
                currentContext.set(null);
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
            e.printStackTrace();
        }
        return null;
    }


    class ContextPoolFactory extends BasePooledObjectFactory<ContextProvider> {
        @Override
        public ContextProvider create() throws Exception {
            System.out.println("ContextPoolFactory.create");
            Context context = Context.newBuilder(JS)
                    .allowHostClassLookup(s -> true)
                    .allowHostAccess(HostAccess.ALL)
                    .allowPolyglotAccess(PolyglotAccess.ALL)
                    .allowIO(true)
                    .engine(sharedEngine).build();

            ContextProvider contextProvider = new ContextProvider(context);

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
                    e.printStackTrace();
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
            // TODO Improve validity check. Should check internal bundle version, and helpers list
            Collection<Bundle> registeredSources = p.getObject().getRegisteredBundles();
            Collection<Bundle> currentSources = initScripts.keySet();

            return registeredSources.containsAll(currentSources) && currentSources.containsAll(registeredSources);
        }

        @Override
        public void destroyObject(PooledObject<ContextProvider> p) throws Exception {
            System.out.println("ContextPoolFactory.destroyObject");
            p.getObject().close();
        }

        @Override
        public void activateObject(PooledObject<ContextProvider> p) throws Exception {
            super.activateObject(p);
        }

        @Override
        public void passivateObject(PooledObject<ContextProvider> p) throws Exception {
            super.passivateObject(p);
        }
    }
}
