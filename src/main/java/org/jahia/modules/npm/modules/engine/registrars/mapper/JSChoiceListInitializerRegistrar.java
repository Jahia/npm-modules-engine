package org.jahia.modules.npm.modules.engine.registrars.mapper;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
import org.jahia.services.content.nodetypes.initializers.ChoiceListValue;
import org.jahia.services.content.nodetypes.initializers.ModuleChoiceListInitializer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.*;

public class JSChoiceListInitializerRegistrar implements JSServiceRegistrar<ModuleChoiceListInitializer> {
    @Override
    public ServiceRegistration<ModuleChoiceListInitializer> register(BundleContext bundleContext, Map<String, Object> jsService, GraalVMEngine engine) {
        return bundleContext.registerService(ModuleChoiceListInitializer.class, new JSChoiceListInitializer(jsService, engine), new Hashtable<>());
    }

    public static class JSChoiceListInitializer implements ModuleChoiceListInitializer {
        private final GraalVMEngine engine;
        private String key;

        public JSChoiceListInitializer(Map<String, Object> value, GraalVMEngine engine) {
            this.engine = engine;
            this.key = (String) value.get("key");
        }

        @Override
        public void setKey(String s) {
            this.key = s;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public List<ChoiceListValue> getChoiceListValues(ExtendedPropertyDefinition extendedPropertyDefinition, String s, List<ChoiceListValue> list, Locale locale, Map<String, Object> map) {
            return engine.doWithContext(contextProvider -> {
                Value jsResult = Value.asValue(getJSValues(contextProvider).get("getChoiceListValues")).execute(extendedPropertyDefinition, s, list, locale, map);
                return convertResult(jsResult);
            });
        }

        private List<ChoiceListValue> convertResult(Value jsResult) {
            if (jsResult.hasArrayElements()) {
                List<ChoiceListValue> result = new ArrayList<>();
                for (Object value : jsResult.as(List.class)) {
                    Map<String, Object> mapValue = (Map<String, Object>) value;
                    result.add(new ChoiceListValue(mapValue.get("displayName").toString(), mapValue.get("value").toString()));
                }
                return result;
            }
            return Collections.emptyList();
        }

        private Map<String, Object> getJSValues(ContextProvider contextProvider) {
            return contextProvider.getRegistry().get("service", key);
        }
    }
}
