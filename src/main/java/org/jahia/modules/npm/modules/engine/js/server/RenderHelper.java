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
package org.jahia.modules.npm.modules.engine.js.server;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.js.injector.OSGiService;
import org.jahia.modules.npm.modules.engine.js.mock.MockPageContext;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.JSNodeMapper;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRSiteNode;
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
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
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

    public ProxyObject getRenderParameters(Resource resource) {
        Map<String, Object> moduleParams = new HashMap<>(resource.getModuleParams());
        return recursiveProxyMap(moduleParams);
    }

    public String escapePath(String path) {
        return Text.escapePath(path);
    }

    public JCRNodeWrapper findDisplayableNode(JCRNodeWrapper node, RenderContext renderContext, JCRSiteNode contextSite) {
        return JCRContentUtils.findDisplayableNode(node, renderContext, contextSite);
    }

    public String renderComponent(Map<String, ?> attr, RenderContext renderContext) throws RepositoryException {
        return jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), renderContext.getWorkspace(),
                renderContext.getMainResource().getLocale(), session -> {

            JCRNodeWrapper node = JSNodeMapper.toVirtualNode((Map<String, ?>) attr.get("content"), session, renderContext);
            String renderConfig = attr.get("advanceRenderingConfig") != null ? (String) attr.get("advanceRenderingConfig") : "";
            String templateType = attr.get("templateType") != null ? (String) attr.get("templateType") : "html";

            if ("OPTION".equals(renderConfig)) {
                Map<String, Object> optionAttr = new HashMap<>();
                optionAttr.put("node", node);
                optionAttr.put("templateType", templateType);
                optionAttr.put("view", attr.get("view"));
                optionAttr.put("parameters", attr.get("parameters"));
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
                    // handle render parameters for JSON rendering
                    Map<String, Object> renderParameters = (Map<String, Object>) attr.get("parameters");
                    if (renderParameters != null && !renderParameters.isEmpty()) {
                        String charset = renderContext.getResponse().getCharacterEncoding();
                        for (Map.Entry<String, Object> renderParam : renderParameters.entrySet()) {
                            // only allow String params for compatibility reasons due to JSP ParamParent parameters being a <String,String> map
                            if (renderParam.getValue() instanceof String) {
                                r.getModuleParams().put(URLDecoder.decode(renderParam.getKey(), charset),
                                        URLDecoder.decode((String) renderParam.getValue(), charset));
                            }
                        }
                    }

                    return renderService.render(r, renderContext);
                } catch (RenderException | IOException e) {
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
            JCRNodeWrapper currentNode = currentResource.getNode();
            if (path.startsWith("/")) {
                if (!currentNode.getSession().nodeExists(path)) {
                    logger.warn("Skipping render of {}, the node doesn't exists", path);
                    return "";
                }
            } else {
                if (!currentNode.hasNode(path)) {
                    logger.warn("Skipping render of {}/{}, the node doesn't exists", currentNode.getPath(), path);
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

    public void addCacheDependencyTag(Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        renderTag(new AddCacheDependencyTag(), attr, renderContext);
    }

    public String renderArea(Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {
        // We copy the attributes to avoid modifying the original map
        Map<String,Object> areaAttr = new HashMap<>(attr);
        // We now have to transform the attr properties into something that the AreaTag can understand
        if (areaAttr.get("path") != null && areaAttr.get("name") != null) {
            logger.warn("Both path and name are set on the area tag, name will be ignored");
            areaAttr.remove("name");
        } else {
            if (areaAttr.get("name") != null) {
                areaAttr.put("path", areaAttr.get("name"));
                areaAttr.remove("name");
            }
        }
        if (areaAttr.get("areaView") != null) {
            areaAttr.put("view", areaAttr.get("areaView"));
            areaAttr.remove("areaView");
        }
        Object allowedTypes = areaAttr.get("allowedTypes");
        if (allowedTypes != null) {
            if (allowedTypes instanceof List) {
                areaAttr.put("nodeTypes", StringUtils.join((List) allowedTypes, ' '));
                areaAttr.remove("allowedTypes");
            } if (allowedTypes.getClass().isArray()) {
                Object[] allowedTypeArray = (Object[]) allowedTypes;
                areaAttr.put("nodeTypes", StringUtils.join(allowedTypeArray, ' '));
                areaAttr.remove("allowedTypes");
            }
        }
        if (areaAttr.get("numberOfItems") != null) {
            areaAttr.put("listLimit", areaAttr.get("numberOfItems"));
            areaAttr.remove("numberOfItems");
        }

        if (areaAttr.get("level") != null || areaAttr.get("limitedAbsoluteAreaEdit") != null) {
            if (!"absoluteArea".equals(areaAttr.get("moduleType"))) {
                areaAttr.put("moduleType", "absoluteArea");
                logger.warn("The level and limitedAbsoluteAreaEdit attributes are only available for absoluteArea moduleType. Forcing moduleType to absoluteArea");
            }
        }

        AreaTag areaTag = new AreaTag();
        // The subNodesView is an attribute that is passed as a tag parameter
        if (areaAttr.get("subNodesView") != null) {
            areaTag.addParameter("subNodesView", (String) areaAttr.get("subNodesView"));
            areaAttr.remove("subNodesView");
        }

        return renderTag(areaTag, areaAttr, renderContext);
    }

    private String renderTag(TagSupport tag, Map<String, Object> attr, RenderContext renderContext) throws IllegalAccessException, InvocationTargetException, JspException {

        Map<String, Serializable> renderParameters = (Map<String, Serializable>) attr.remove("parameters");
        if (tag instanceof ParamParent && renderParameters != null && !renderParameters.isEmpty()) {
            for (Map.Entry<String, Serializable> tagParam : renderParameters.entrySet()) {
                // only allow String params due to ParamParent parameters being a <String,String> map
                if (tagParam.getValue() instanceof String) {
                    ((ParamParent) tag).addParameter(tagParam.getKey(), (String) tagParam.getValue());
                }
            }
        }

        BeanUtils.populate(tag, attr);
        MockPageContext pageContext = new MockPageContext(renderContext);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();
        try {
            pageContext.flushWriters();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pageContext.getTargetWriter().getBuffer().toString();
    }

    private ProxyObject recursiveProxyMap(Map<String, Object> mapToProxy) {
        for (Map.Entry<String, Object> entry : mapToProxy.entrySet()) {
            if (entry.getValue() instanceof Map) {
                mapToProxy.put(entry.getKey(), recursiveProxyMap((Map<String, Object>) entry.getValue()));
            }
            if (entry.getValue() instanceof Collection) {
                mapToProxy.put(entry.getKey(), ProxyArray.fromList((List<Object>) ((Collection) entry.getValue()).stream().map(o -> {
                    if (o instanceof Map) {
                        return recursiveProxyMap((Map<String, Object>) o);
                    }
                    return o;
                }).collect(Collectors.toList())));
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
