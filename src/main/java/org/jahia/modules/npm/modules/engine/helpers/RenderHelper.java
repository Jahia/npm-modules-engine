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
package org.jahia.modules.npm.modules.engine.helpers;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jahia.api.Constants;
import org.jahia.modules.graphql.provider.dxm.node.NodeHelper;
import org.jahia.modules.npm.modules.engine.helpers.injector.OSGiService;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.nodetypes.ExtendedNodeDefinition;
import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class RenderHelper {
    private static final Logger logger = LoggerFactory.getLogger(RenderHelper.class);

    private JCRSessionFactory jcrSessionFactory;
    private JCRTemplate jcrTemplate;
    private RenderService renderService;

    private final ContextProvider context;

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
        return jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), renderContext.getWorkspace(), renderContext.getMainResource().getLocale(), session -> {
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
            if (definition.containsKey("mixins")) {
                Object mixins = definition.get("mixins");
                if (mixins instanceof String) {
                    node.addMixin((String) mixins);
                } else if (mixins instanceof List<?>) {
                    for (Object mixinName : (List<?>) mixins) {
                        node.addMixin(mixinName.toString());
                    }
                }
            }
            processProperties(node, (Map<String, ?>) definition.get("properties"));

            handleBoundComponent(node, renderContext, session, (String) definition.get("boundComponentRelativePath"));
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
            // TODO TECH-1335 use TO_CACHE_WITH_PARENT_FRAGMENT constant once minimal jahia version >= 8.2.0.0
            r.getModuleParams().put("toCacheWithParentFragment", true);
            try {
                return renderService.render(r, renderContext);
            } catch (RenderException e) {
                throw new RepositoryException(e);
            }
        });
    }

    private void processProperties(JCRNodeWrapper node, Map<String, ?> properties) throws RepositoryException {
        if (properties != null) {
            for (Map.Entry<String, ?> entry : properties.entrySet()) {
                ExtendedPropertyDefinition propertyDefinition = node.getApplicablePropertyDefinition(entry.getKey());
                if (propertyDefinition != null) {
                    processSimpleProperty(node, entry, propertyDefinition.isMultiple());
                } else if (entry.getValue() instanceof Map) {
                    processChildNode(node, entry.getKey(), (Map<String, ?>) entry.getValue());
                } else if (entry.getValue() instanceof List) {
                    processChildNodeList(node, entry);
                }
            }
        }
    }

    private void processSimpleProperty(JCRNodeWrapper node, Map.Entry<String, ?> entry, boolean multiple) throws RepositoryException {
        if (multiple) {
            if (entry.getValue() instanceof List && ((List) entry.getValue()).size() > 0) {
                List<?> values = (List<?>) entry.getValue();
                List<String> stringList = values.stream().map(Object::toString).collect(Collectors.toUnmodifiableList());
                node.setProperty(entry.getKey(), stringList.toArray(new String[stringList.size()]));
            } else {
                node.setProperty(entry.getKey(), ((String) entry.getValue()).split(" "));
            }
        } else {
            node.setProperty(entry.getKey(), (String) entry.getValue());
        }
    }

    private void processChildNode(JCRNodeWrapper node, String childKey, Map<String, ?> childProperties) throws RepositoryException {
        String primaryNodeType = (String) childProperties.get(Constants.JCR_PRIMARYTYPE);
        childProperties.remove(Constants.JCR_PRIMARYTYPE);
        ExtendedNodeDefinition nodeDefinition = node.getApplicableChildNodeDefinition(childKey, primaryNodeType);
        if (nodeDefinition != null) {
            JCRNodeWrapper child = node.addNode(childKey, primaryNodeType);
            processProperties(child, childProperties);
        }
    }

    private void processI18nNodes(JCRNodeWrapper node, List<?> children, String key) {
        children.stream().forEach(child -> {
            Map<String, ?> childrenProperties = (Map<String, ?>) child;
            String language = (String) childrenProperties.get("language");
            childrenProperties.remove("language");
            try {
                for (Map.Entry<String, ?> p : childrenProperties.entrySet()) {
                    node.getOrCreateI18N(new Locale(language)).setProperty(p.getKey(), (String) p.getValue());
                }
            } catch (RepositoryException e) {
                logger.error("Error while processing {} for parent {}", key, node.getPath(), e);
            }
        });
    }

    private void processChildNodeList(JCRNodeWrapper node, Map.Entry<String, ?> entry) {
        List<?> children = (List<?>) entry.getValue();
        if (entry.getKey().equals("i18n")) {
            processI18nNodes(node, children, entry.getKey());
        }
    }

    private void handleBoundComponent(JCRNodeWrapper currentNode, RenderContext renderContext, JCRSessionWrapper session, String boundComponentRelativePath) {
        try {
            if (currentNode.isNodeType("jmix:bindedComponent") && StringUtils.isNotEmpty(boundComponentRelativePath)) {
                String boundComponentPath = renderContext.getMainResource().getNodePath().concat(boundComponentRelativePath);
                JCRNodeWrapper boundComponent = session.getNode(boundComponentPath);
                renderContext.getMainResource().getDependencies().add(boundComponent.getPath());
                currentNode.setProperty("j:bindedComponent", boundComponent);
            }
        } catch (RepositoryException e) {
            logger.error("Error while getting bound component: {}", e.getMessage());
        }
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
        tag.doStartTag();
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
