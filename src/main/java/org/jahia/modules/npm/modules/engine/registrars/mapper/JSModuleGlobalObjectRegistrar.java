package org.jahia.modules.npm.modules.engine.registrars.mapper;

import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.modules.npm.modules.engine.rules.JSGlobalObject;
import org.jahia.services.content.rules.ModuleGlobalObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;

public class JSModuleGlobalObjectRegistrar implements JSServiceRegistrar<ModuleGlobalObject>{
    @Override
    public ServiceRegistration<ModuleGlobalObject> register(BundleContext bundleContext, Map<String, Object> jsService, GraalVMEngine engine) {
        return bundleContext.registerService(ModuleGlobalObject.class, new JSModuleGlobalObject(jsService, engine), new Hashtable<>());
    }

    public static class JSModuleGlobalObject extends ModuleGlobalObject implements JSServiceClosable {

        private final String key;
        public JSModuleGlobalObject(Map<String, Object> value, GraalVMEngine engine) {
            this.key = value.get("key").toString();

            JSGlobalObject jsGlobalObject = new JSGlobalObject(key, engine);
            getGlobalRulesObject().put(key, jsGlobalObject);
        }

        @Override
        public void close() {
            getGlobalRulesObject().remove(key);
        }
    }
}
