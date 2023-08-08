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
import org.jahia.modules.npm.modules.engine.helpers.injector.OSGiService;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
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
        Locale currentLocale = renderContext.getMainResource().getLocale();
        return jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), renderContext.getWorkspace(), currentLocale, session -> {
            String path = (String) definition.get("path");

            if (path == null) {
                path = "/";
            }
            JCRNodeWrapper parent = session.getNode(path);

            JCRNodeWrapper node;
            if (definition.get("content") != null) {
                node = transformJSONNodeToJCRNode((Map<String, ?>) definition.get("content"), parent, currentLocale);
            } else {
                // TODO: to be removed to only allow usage with JSON node (content param).
                node = transformJSONNodeToJCRNode(definition, parent, currentLocale);
            }

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

    private JCRNodeWrapper transformJSONNodeToJCRNode(Map<String, ?> jsonNode, JCRNodeWrapper parent, Locale currentLocale) throws RepositoryException {
        // TODO: stop support primaryNodeType
        String nodeType = jsonNode.containsKey("nodeType") ? (String) jsonNode.get("nodeType") : (String) jsonNode.get("primaryNodeType");
        // TODO: make name mandatory and stop using temp-node
        String name = jsonNode.containsKey("name") ? (String) jsonNode.get("name") : "temp-node";
        JCRNodeWrapper node = parent.addNode(name, nodeType);

        // handle mixins
        if (jsonNode.containsKey("mixins")) {
            Object mixins = jsonNode.get("mixins");
            if (mixins instanceof String) {
                node.addMixin((String) mixins);
            } else if (mixins instanceof List<?>) {
                for (Object mixinName : (List<?>) mixins) {
                    node.addMixin(mixinName.toString());
                }
            }
        }

        // handle properties
        Map<String, ?> properties = (Map<String, ?>) jsonNode.get("properties");
        if (properties != null) {
            for (Map.Entry<String, ?> entry : properties.entrySet()) {
                setProperty(node, entry.getKey(), entry.getValue());
            }
        }

        // handle i18n properties
        Map<String, ?> i18nProperties = (Map<String, ?>) jsonNode.get("i18nProperties");
        if (i18nProperties != null) {
            for (Map.Entry<String, ?> entry : i18nProperties.entrySet()) {
                Locale locale = new Locale(entry.getKey());
                if (currentLocale.equals(locale)) {
                    Map<String, ?> localeProperties = (Map<String, ?>) entry.getValue();
                    for (Map.Entry<String, ?> localeProperty : localeProperties.entrySet()) {
                        setProperty(node, localeProperty.getKey(), localeProperty.getValue());
                    }
                }
            }
        }

        // handle children
        List<Map<String, ?>> children = (List<Map<String, ?>>) jsonNode.get("children");
        if (children != null) {
            for (Map<String, ?> child : children) {
                transformJSONNodeToJCRNode(child, node, currentLocale);
            }
        }

        return node;
    }

    private void setProperty(JCRNodeWrapper node, String propertyName, Object value) throws RepositoryException {
        ExtendedPropertyDefinition epd = node.getApplicablePropertyDefinition(propertyName);
        if (epd != null && epd.isMultiple()) {
            if (value instanceof List && ((List) value).size() > 0) {
                List<?> values = (List<?>) value;
                List<String> stringList = values.stream().map(Object::toString).collect(Collectors.toUnmodifiableList());
                node.setProperty(propertyName, stringList.toArray(new String[stringList.size()]));
            } else {
                node.setProperty(propertyName, ((String) value).split(" "));
            }
        } else {
            node.setProperty(propertyName, (String) value);
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
