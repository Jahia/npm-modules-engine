package org.jahia.modules.npm.modules.engine.registrars.mapper;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.cache.CacheKeyPartGenerator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

public class JSCacheKeyPartGeneratorRegistrar implements JSServiceRegistrar<CacheKeyPartGenerator>{
    @Override
    public ServiceRegistration<CacheKeyPartGenerator> register(BundleContext bundleContext, Map<String, Object> jsService, GraalVMEngine engine) {
        return bundleContext.registerService(CacheKeyPartGenerator.class, new JSCacheKeyPartGenerator(jsService, engine), new Hashtable<>());
    }

    public static class JSCacheKeyPartGenerator implements CacheKeyPartGenerator {
        private final GraalVMEngine engine;
        private final String key;

        public JSCacheKeyPartGenerator(Map<String, Object> value, GraalVMEngine engine) {
            this.engine = engine;
            this.key = value.get("key").toString();
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue(Resource resource, RenderContext renderContext, Properties properties) {
            return engine.doWithContext(contextProvider -> {
                return Value.asValue(getJSValues(contextProvider).get("getValue")).execute(resource, renderContext, properties).asString();
            });
        }

        @Override
        public String replacePlaceholders(RenderContext renderContext, String s) {
            return engine.doWithContext(contextProvider -> {
                return Value.asValue(getJSValues(contextProvider).get("replacePlaceholders")).execute(renderContext, s).asString();
            });
        }

        private Map<String, Object> getJSValues(ContextProvider contextProvider) {
            return contextProvider.getRegistry().get("service", key);
        }
    }
}
