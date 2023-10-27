/*
 * Copyright (C) 2002-2023 Jahia Solutions Group SA. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jahia.modules.npm.modules.engine.jsengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.graalvm.polyglot.*;
import org.jahia.data.templates.JahiaTemplatesPackage;
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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base JS engine based on GraalVM
 */
@Component(service = GraalVMEngine.class, immediate = true)
public class GraalVMEngine {
    private static final Logger logger = LoggerFactory.getLogger(GraalVMEngine.class);

    public static final String JS = "js";

    private Engine sharedEngine;

    private JSGlobalVariableFactory globals;

    private GenericObjectPool<ContextProvider> pool;
    private final ThreadLocal<Stack<ContextProvider>> currentContext = ThreadLocal.withInitial(Stack::new);

    private final LinkedHashMap<Bundle, Source> initScripts = new LinkedHashMap<>();
    private final AtomicInteger version = new AtomicInteger(0);

    private BundleContext bundleContext;

    @Reference(service = JSGlobalVariableFactory.class, cardinality = ReferenceCardinality.MANDATORY)
    public void bindVariable(JSGlobalVariableFactory globals) {
        this.globals = globals;
    }

    public void enableBundle(Bundle bundle) {
        String script = getBundleScript(bundle);
        if (script != null) {
            try {
                initScripts.put(bundle, getGraalSource(bundle, script));
                version.incrementAndGet();
                logger.info("Registered bundle {} in GraalVM engine", bundle.getSymbolicName());
            } catch (IOException ioe) {
                logger.error("Error enabling bundle {}", bundle.getSymbolicName(), ioe);
            }
        }
    }

    public void disableBundle(Bundle bundle) {
        if (initScripts.remove(bundle) != null) {
            version.incrementAndGet();
            logger.info("Unregistered bundle {} from GraalVM engine", bundle.getSymbolicName());
        }
    }

    @Activate
    public void activate(BundleContext bundleContext, Map<String, ?> props) {
        logger.debug("GraalVMEngine.activate");
        this.bundleContext = bundleContext;
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
                String opt = StringUtils.substringAfter(entry.getKey(), "polyglot.");
                builder.option(opt, entry.getValue().toString());
            }
        }
        sharedEngine = builder.build();
        initializePool();
    }

    @Deactivate
    public void deactivate() {
        logger.debug("GraalVMEngine.deactivate");
        pool.close();
        sharedEngine.close();
    }

    public <T> T doWithContext(Function<ContextProvider, T> callback) {
        Stack<ContextProvider> cx = currentContext.get();
        if (!cx.isEmpty()) {
            return callback.apply(cx.peek());
        } else {
            try {
                cx.push(pool.borrowObject());
            } catch (Exception e) {
                throw new GraalVMException("Unable to borrow context from pool: " + e.getMessage(), e);
            }
            try {
                return callback.apply(cx.peek());
            } finally {
                pool.returnObject(cx.pop());
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
            throw new IOException("Cannot get resource " + bundle.getSymbolicName() + " / " + script);
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
                    return IOUtils.toString(sources.getNode(path).getFileContent().downloadFile(), Charset.defaultCharset());
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
                    .orElseGet(ThrowingSupplier.unchecked(() -> bundle.getResource(path) != null ? IOUtils.toString(bundle.getResource(path), Charset.defaultCharset()) : null));
        } catch (Exception e) {
            logger.error("Cannot get resource", e);
        }
        return null;
    }

    private void initializePool() {
        GenericObjectPoolConfig<ContextProvider> config = new GenericObjectPoolConfig<>();
        config.setTestOnBorrow(true);
        pool = new GenericObjectPool<>(new ContextPoolFactory(), config);
    }

    private String getBundleScript(Bundle bundle) {
        URL url = bundle.getResource("package.json");
        if (url != null) {
            try {
                String content = IOUtils.toString(url);
                ObjectMapper mapper = new ObjectMapper();
                Map<?, ?> json = mapper.readValue(content, Map.class);
                Map<?, ?> jahia = (Map<?, ?>) json.get("jahia");
                if (jahia != null && jahia.containsKey("server")) {
                    return (String) jahia.get("server");
                }
            } catch (IOException ioe) {
                logger.error("Error accessing bundle {} package.json file", bundle.getSymbolicName(), ioe);
            }
        }
        return null;
    }

    public List<Bundle> getNPMBundles() {
        return Arrays.stream(bundleContext.getBundles())
                .filter(bundle -> bundle.getState() == Bundle.ACTIVE && isNPMBundle(bundle))
                .collect(Collectors.toList());
    }

    public boolean isNPMBundle(Bundle bundle) {
        return bundle.getBundleId() != bundleContext.getBundle().getBundleId() &&
                        getBundleScript(bundle) != null;
    }

    class ContextPoolFactory extends BasePooledObjectFactory<ContextProvider> {
        @Override
        public ContextProvider create() throws Exception {
            logger.debug("ContextPoolFactory.create");
            Context context = Context.newBuilder(JS)
                    .allowHostClassLookup(s -> true)
                    .allowHostAccess(HostAccess.ALL)
                    .allowPolyglotAccess(PolyglotAccess.ALL)
                    .allowIO(true)
                    .engine(sharedEngine).build();

            ContextProvider contextProvider = new ContextProvider(context, version.get());

            // Inject globals object into the context
            if (globals != null) {
                context.getBindings(JS).putMember(globals.getName(), globals.getObject(contextProvider));
            }

            // Initialize context with available Server side JS from bundles
            for (Map.Entry<Bundle, Source> entry : initScripts.entrySet()) {
                try {
                    // Here we inject the bundle because registry is keeping track of witch bundle is registering stuff.
                    context.getBindings(JS).putMember("bundle", entry.getKey());
                    context.eval(entry.getValue());
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
            logger.debug("ContextPoolFactory.destroyObject");
            p.getObject().close();
        }
    }
}
