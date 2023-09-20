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
import org.apache.commons.lang.StringUtils;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.helpers.injector.OSGiService;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.JSNodeMapper;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    public ProxyObject transformToJsNode(JCRNodeWrapper node, boolean includeChildren, boolean includeDescendants, boolean includeAllTranslations) throws RepositoryException {
        return recursiveProxyMap(JSNodeMapper.toJSNode(node, includeChildren, includeDescendants, includeAllTranslations));
    }

    public String renderComponent(Map<String, ?> attr, RenderContext renderContext) throws RepositoryException {
        if (!attr.containsKey("content")) {
            return "";
        }

        return jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), renderContext.getWorkspace(),
                renderContext.getMainResource().getLocale(), session -> {

            JCRNodeWrapper node = JSNodeMapper.toVirtualNode((Map<String, ?>) attr.get("content"), session, renderContext);
            String renderConfig = attr.get("advanceRenderingConfig") != null ? (String) attr.get("advanceRenderingConfig") : "";
            String templateType = attr.containsKey("templateType") ? (String) attr.get("templateType") : "html";

            if ("OPTION".equals(renderConfig)) {
                Map<String, Object> optionAttr = new HashMap<>();
                optionAttr.put("node", node);
                optionAttr.put("templateType", templateType);
                optionAttr.put("view", attr.get("view"));
                optionAttr.put("nodetype", node.getPrimaryNodeType().getName());
                try {
                    return renderTag(new OptionTag(), optionAttr, renderContext);
                } catch (IllegalAccessException | InvocationTargetException | JspException e) {
                    throw new RepositoryException(e);
                }
            } else {
                Resource r = new Resource(node, templateType, (String) attr.get("view"),
                        "INCLUDE".equals(renderConfig) ? "include" : "module");
                // TODO TECH-1335 use TO_CACHE_WITH_PARENT_FRAGMENT constant once minimal jahia version >= 8.2.0.0
                r.getModuleParams().put("toCacheWithParentFragment", true);
                try {
                    return renderService.render(r, renderContext);
                } catch (RenderException e) {
                    throw new RepositoryException(e);
                }
            }
        });
    }

    public String createContentButtons(String childName, String nodeTypes, boolean editCheck, RenderContext renderContext, Resource currentResource) throws JspException, InvocationTargetException, IllegalAccessException, RepositoryException {

        boolean childExists = !"*".equals(childName) && currentResource.getNode().hasNode(childName);
        if (!childExists && (!editCheck || renderContext.isEditMode())) {
            Map<String, Object> attr = new HashMap<>();
            attr.put("path", childName);
            attr.put("nodeTypes", StringUtils.isNotEmpty(nodeTypes) ? nodeTypes : "");
            return renderTag(new ModuleTag(), attr, renderContext);
        }
        return "";
    }

    public String render(Map<String, Object> attr, RenderContext renderContext, Resource currentResource) throws JspException, InvocationTargetException, IllegalAccessException, RepositoryException {

        // handle json node
        if (attr.get("content") != null) {
            return renderComponent(attr, renderContext);
        }
        // if the child node requested is not available, return an empty string
        // This make the path parameter mandatory, that's why is passed as a dedicated param.
        String path = (String) attr.get("path");
        if (path != null) {
            if (path.startsWith("/")) {
                if (!currentResource.getNode().getSession().nodeExists(path)) {
                    return "";
                }
            } else {
                if (!currentResource.getNode().hasNode(path)) {
                    return "";
                }
            }
        }

        String renderConfig = attr.get("advanceRenderingConfig") != null ? (String) attr.get("advanceRenderingConfig") : "";
        switch (renderConfig) {
            case "INCLUDE": {
                return renderTag(new IncludeTag(), attr, renderContext);
            }
            case "OPTION": {
                return renderTag(new OptionTag(), attr, renderContext);
            }
            default: {
                return renderTag(new ModuleTag(), attr, renderContext);
            }
        }
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

    private ProxyObject recursiveProxyMap(Map<String, Object> mapToProxy) {
        for (Map.Entry<String, Object> entry : mapToProxy.entrySet()) {
            if (entry.getValue() instanceof Map) {
                mapToProxy.put(entry.getKey(), recursiveProxyMap((Map<String, Object>) entry.getValue()));
            }
            if (entry.getValue() instanceof Collection) {
                mapToProxy.put(entry.getKey(), ((Collection) entry.getValue()).stream().map(o -> {
                    if (o instanceof Map) {
                        return recursiveProxyMap((Map<String, Object>) o);
                    }
                    return o;
                }).collect(Collectors.toList()));
            }
        }
        return ProxyObject.fromMap(mapToProxy);
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
