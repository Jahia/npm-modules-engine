package org.jahia.modules.npmplugins.registrars;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npmplugins.helpers.RegistryHelper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.*;

@Component(service = Registrar.class, immediate = true)
public class RenderFilterRegistrar implements Registrar {

    private RenderService renderService;
    private BundleContext bundleContext;

    private Map<Bundle, Collection<ServiceRegistration<RenderFilter>>> registrations = new HashMap<>();

    @Reference
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void register(RegistryHelper registry, Bundle bundle) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", "render-filter");
        filter.put("bundle", bundle);

        Set<ServiceRegistration<RenderFilter>> set = new HashSet<>();
        registrations.put(bundle, set);

        List<Map<String, Object>> renderFilters = registry.getRegistry().find(filter);
        for (Map<String, Object> renderFilter : renderFilters) {
            RenderFilterBridge renderFilterImpl = new RenderFilterBridge(Value.asValue(renderFilter));
            renderFilterImpl.setRenderService(renderService);
            renderFilterImpl.setPriority(0);

            set.add(bundleContext.registerService(RenderFilter.class, renderFilterImpl, new Hashtable<>()));
        }
    }

    @Override
    public void unregister(RegistryHelper registry, Bundle bundle) {
        Collection<ServiceRegistration<RenderFilter>> set = registrations.get(bundle);
        if (set != null) {
            for (ServiceRegistration<RenderFilter> registration : set) {
                registration.unregister();
            }
        }
    }

    public static class RenderFilterBridge extends AbstractFilter {
        private Value value;

        public RenderFilterBridge(Value value) {
            this.value = value;
            if (value.hasMember("priority")) {
                setPriority(value.getMember("priority").asInt());
            }
            if (value.hasMember("description")) {
                setDescription(value.getMember("description").asString());
            }
            if (value.hasMember("applyOnConfigurations")) {
                this.setApplyOnConfigurations(value.getMember("applyOnConfigurations").asString());
            }
            if (value.hasMember("applyOnModes")) {
                this.setApplyOnModes(value.getMember("applyOnModes").asString());
            }
            if (value.hasMember("applyOnNodeTypes")) {
                this.setApplyOnNodeTypes(value.getMember("applyOnNodeTypes").asString());
            }
            if (value.hasMember("applyOnTemplates")) {
                this.setApplyOnTemplates(value.getMember("applyOnTemplates").asString());
            }
            if (value.hasMember("applyOnTemplateTypes")) {
                this.setApplyOnTemplateTypes(value.getMember("applyOnTemplateTypes").asString());
            }
        }

        @Override
        public String execute(String s, RenderContext renderContext, Resource resource, RenderChain renderChain) throws Exception {
            return value.getMember("execute").execute(s, renderContext, resource, renderChain).asString();
        }

        @Override
        public String prepare(RenderContext renderContext, Resource resource, RenderChain renderChain) throws Exception {
            return value.getMember("prepare").execute(renderContext, resource, renderChain).asString();
        }
    }
}
