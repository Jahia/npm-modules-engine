package org.jahia.modules.npmplugins.helpers;

import org.apache.commons.beanutils.BeanUtils;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.JSGlobalVariable;
import org.jahia.modules.npmplugins.jsengine.Promise;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderException;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.Resource;
import org.jahia.taglibs.template.include.ModuleTag;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import java.util.Map;

@Component(service = JSGlobalVariable.class, immediate = true)
public class RenderHelper implements JSGlobalVariable {

    private JCRSessionFactory jcrSessionFactory;
    private JCRTemplate jcrTemplate;
    private RenderService renderService;

    @Reference
    public void setJcrSessionFactory(JCRSessionFactory jcrSessionFactory) {
        this.jcrSessionFactory = jcrSessionFactory;
    }

    @Reference
    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Reference
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Override
    public String getName() {
        return "renderHelper";
    }

    @Override
    public Object getInstance(ContextProvider context) {
        return new Instance(context);
    }

    public class Instance {
        private ContextProvider context;

        public Instance(ContextProvider context) {
            this.context = context;
        }

        public Promise render(Map parameters) {
            return (onResolve, onReject) -> {
                try {
                    RenderContext renderContext = (RenderContext) parameters.get("renderContext");
                    String path = (String) parameters.get("path");
                    String template = (String) parameters.get("template");
                    String templateType = (String) parameters.get("templateType");
                    String contextConfiguration = (String) parameters.get("contextConfiguration");

                    JCRNodeWrapper node = jcrSessionFactory.getCurrentUserSession(renderContext.getWorkspace(), renderContext.getMainResource().getLocale()).getNode(path);
                    Resource resource = new Resource(node, templateType != null ? templateType : "html", template, contextConfiguration != null ? contextConfiguration : "module");
                    onResolve.execute(renderService.render(resource, renderContext));
                } catch (Exception e) {
                    onReject.execute(e.getMessage());
                }
            };
        }

        public Promise renderModule(Map attr, RenderContext renderContext) {
            return (onResolve, onReject) -> {
                try {
                    ModuleTag moduleTag = new ModuleTag();
                    BeanUtils.populate(moduleTag, attr);
                    MockPageContext context = new MockPageContext(renderContext);
                    moduleTag.setPageContext(context);
                    moduleTag.doEndTag();
                    onResolve.execute(context.getTargetWriter().getBuffer().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    onReject.execute(e.getMessage());
                }
            };
        }

        public Promise renderComponent(Map<String, ?> definition, RenderContext renderContext) {
            return (onResolve, onReject) -> {
                try {
                    String res = jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), null, jcrSessionFactory.getCurrentLocale(), session -> {
                        String path = (String) definition.get("path");

                        if (path == null) {
                            path = renderContext.getMainResource().getNodePath();
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
                            contextConfiguration = "preview";
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
                    onResolve.execute(res);
                } catch (Exception e) {
                    e.printStackTrace();
                    onReject.execute(e.getMessage());
                }
            };
        }
    }

}
