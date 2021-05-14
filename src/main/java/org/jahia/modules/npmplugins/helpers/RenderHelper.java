package org.jahia.modules.npmplugins.helpers;

import org.apache.commons.beanutils.BeanUtils;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderException;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.Resource;
import org.jahia.taglibs.template.include.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class RenderHelper {
    private static final Logger logger = LoggerFactory.getLogger(RenderHelper.class);

    private JCRSessionFactory jcrSessionFactory;
    private JCRTemplate jcrTemplate;
    private RenderService renderService;

    private ContextProvider context;

    public RenderHelper(ContextProvider context) {
        this.context = context;
    }

    public String renderNode(Map<String, Object> parameters) throws RepositoryException, RenderException {
        RenderContext renderContext = (RenderContext) parameters.get("renderContext");
        String path = (String) parameters.get("path");
        String template = (String) parameters.get("template");
        String templateType = (String) parameters.get("templateType");
        String contextConfiguration = (String) parameters.get("contextConfiguration");

        JCRNodeWrapper node = jcrSessionFactory.getCurrentUserSession(renderContext.getWorkspace(), renderContext.getMainResource().getLocale()).getNode(path);
        Resource resource = new Resource(node, templateType != null ? templateType : "html", template, contextConfiguration != null ? contextConfiguration : "module");
        return renderService.render(resource, renderContext);
    }

    public String renderModule(Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        return renderTag(new ModuleTag(), attr, renderContext);
    }

    public String renderInclude(Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        return renderTag(new IncludeTag(), attr, renderContext);
    }

    public String renderOption(Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        return renderTag(new OptionTag(), attr, renderContext);
    }

    public String renderComponent(Map<String, ?> definition, RenderContext renderContext) throws RepositoryException {
        return jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), null, renderContext.getMainResource().getLocale(), session -> {
            String path = (String) definition.get("path");

            if (path == null) {
                path = "/";
            }
            JCRNodeWrapper parent = session.getNode(path);

            String name = (String) definition.get("name");
            if (name == null) {
                name = "temp-node";
            }

            JCRNodeWrapper node = parent.addNode(name, (String) definition.get("primaryNodeType"));
            Map<String, ?> properties = (Map<String, ?>) definition.get("properties");
            if (properties != null) {
                for (Map.Entry<String, ?> entry : properties.entrySet()) {
                    node.setProperty(entry.getKey(), (String) entry.getValue());
                }
            }

            String view = (String) definition.get("view");
            String templateType = (String) definition.get("templateType");
            String contextConfiguration = (String) definition.get("contextConfiguration");

            if (contextConfiguration == null) {
                contextConfiguration = "module";
            }
            if (templateType == null) {
                templateType = "html";
            }

            Resource r = new Resource(node, templateType, view, contextConfiguration);

            try {
                return renderService.render(r, renderContext);
            } catch (RenderException e) {
                throw new RepositoryException(e);
            }
        });
    }

    public String renderSimpleComponent(String name, String type, RenderContext renderContext) throws RepositoryException {
        Map<String, String> definition = new HashMap<>();
        definition.put("name", name);
        definition.put("primaryNodeType", type);
        return renderComponent(definition, renderContext);
    }

    public String addResourcesTag(Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        return renderTag(new AddResourcesTag(), attr, renderContext);
    }

    public String addCacheDependencyTag(Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        return renderTag(new AddCacheDependencyTag(), attr, renderContext);
    }

    private String renderTag(TagSupport tag, Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        BeanUtils.populate(tag, attr);
        MockPageContext pageContext = new MockPageContext(renderContext);
        tag.setPageContext(pageContext);
        tag.doEndTag();
        return pageContext.getTargetWriter().getBuffer().toString();
    }

    @Inject
    @OSGiService
    public void setJcrSessionFactory(JCRSessionFactory jcrSessionFactory) {
        this.jcrSessionFactory = jcrSessionFactory;
    }

    @Inject
    @OSGiService
    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Inject
    @OSGiService
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }
}
