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
            RenderFilterBridge renderFilterImpl = new RenderFilterBridge(renderFilter);
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
        // todo: Store type/key and reuse new context to call operations
        private Map<String,Object> value;

        public RenderFilterBridge(Map<String,Object> value) {
            this.value = value;
            if (value.containsKey("priority")) {
                setPriority(Integer.parseInt(value.get("priority").toString()));
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
            return Value.asValue(value.get("execute")).execute(s, renderContext, resource, renderChain).asString();
        }

        @Override
        public String prepare(RenderContext renderContext, Resource resource, RenderChain renderChain) throws Exception {
            return Value.asValue(value.get("prepare")).execute(renderContext, resource, renderChain).asString();
        }
    }
}
