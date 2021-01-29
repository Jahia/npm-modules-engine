package org.jahia.modules.npmplugins.views;

import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.jahia.services.render.scripting.Script;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service = RenderFilter.class)
public class SimpleTemplateNodeFilter extends AbstractFilter {

    public static final String TEMPLATE_NAME = "j:templateName";

    @Activate
    public void activate() {
        setPriority(20.5f);
        setSkipOnConfigurations("wrappedcontent,wrapper");
        setApplyOnTemplateTypes("html,html-.*");
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        Script script = (Script) renderContext.getRequest().getAttribute("script");

        if (resource.getNode().hasProperty(TEMPLATE_NAME)) {
            String oldTemplate = resource.getTemplate();
            try {
                if ("default".equals(resource.getTemplate()) && resource.getNode().hasProperty(TEMPLATE_NAME)) {
                    resource.setTemplate(resource.getNode().getProperty(TEMPLATE_NAME).getString());
                }
                script = service.resolveScript(resource, renderContext);
                renderContext.getRequest().setAttribute("script", script);
            } finally {
                resource.setTemplate(oldTemplate);
            }
        }

        if (Boolean.parseBoolean(script.getView().getProperties().getProperty("fullPageAllowed"))) {
            chain.pushAttribute(renderContext.getRequest(), "inWrapper", Boolean.TRUE);
            chain.pushAttribute(renderContext.getRequest(),"skipWrapper",  Boolean.TRUE);
            chain.pushAttribute(renderContext.getRequest(),"templateSet",  Boolean.TRUE);
        }
        return null;
    }

}
