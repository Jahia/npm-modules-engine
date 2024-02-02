package org.jahia.modules.npm.modules.engine.registrars.mapper;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;

public class JSRenderFilterRegistrar implements JSServiceRegistrar<RenderFilter> {

    @Override
    public ServiceRegistration<RenderFilter> register(BundleContext bundleContext, Map<String, Object> jsService, GraalVMEngine engine) {
        return bundleContext.registerService(RenderFilter.class, new JSRenderFilter(jsService, engine), new Hashtable<>());
    }

    public static class JSRenderFilter extends AbstractFilter {
        private final GraalVMEngine engine;
        private final String key;

        public JSRenderFilter(Map<String, Object> value, GraalVMEngine engine) {
            this.engine = engine;
            this.key = (String) value.get("key");
            if (value.containsKey("priority")) {
                setPriority(Float.parseFloat(value.get("priority").toString()));
            } else {
                setPriority(0);
            }
            if (value.containsKey("description")) {
                setDescription(value.get("description").toString());
            }
            if (value.containsKey("applyOnConfigurations")) {
                this.setApplyOnConfigurations(value.get("applyOnConfigurations").toString());
            }
            if (value.containsKey("applyOnModes")) {
                this.setApplyOnModes(value.get("applyOnModes").toString());
            }
            if (value.containsKey("applyOnNodeTypes")) {
                this.setApplyOnNodeTypes(value.get("applyOnNodeTypes").toString());
            }
            if (value.containsKey("applyOnTemplates")) {
                this.setApplyOnTemplates(value.get("applyOnTemplates").toString());
            }
            if (value.containsKey("applyOnTemplateTypes")) {
                this.setApplyOnTemplateTypes(value.get("applyOnTemplateTypes").toString());
            }
        }

        @Override
        public String execute(String s, RenderContext renderContext, Resource resource, RenderChain renderChain) throws Exception {
            return engine.doWithContext(contextProvider -> {
                return Value.asValue(getJSValues(contextProvider).get("execute")).execute(s, renderContext, resource, renderChain).asString();
            });
        }

        @Override
        public String prepare(RenderContext renderContext, Resource resource, RenderChain renderChain) throws Exception {
            return engine.doWithContext(contextProvider -> {
                return Value.asValue(getJSValues(contextProvider).get("prepare")).execute(renderContext, resource, renderChain).asString();
            });
        }

        private Map<String, Object> getJSValues(ContextProvider contextProvider) {
            return contextProvider.getRegistry().get("service", key);
        }
    }
}
