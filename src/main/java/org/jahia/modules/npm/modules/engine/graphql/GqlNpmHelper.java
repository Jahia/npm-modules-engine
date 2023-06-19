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
package org.jahia.modules.npm.modules.engine.graphql;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.schema.DataFetchingEnvironment;
import org.jahia.bin.Render;
import org.jahia.modules.graphql.provider.dxm.node.GqlJcrNodeInput;
import org.jahia.modules.graphql.provider.dxm.node.GqlJcrPropertyInput;
import org.jahia.modules.graphql.provider.dxm.osgi.annotations.GraphQLOsgiService;
import org.jahia.modules.graphql.provider.dxm.util.ContextUtil;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderException;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.Resource;
import org.jahia.services.uicomponents.bean.editmode.EditConfiguration;
import org.jahia.utils.LanguageCodeConverters;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;

public class GqlNpmHelper {
    private JCRSessionFactory jcrSessionFactory;
    private JCRTemplate jcrTemplate;
    private RenderService renderService;

    @Inject
    @GraphQLOsgiService
    public void setJcrSessionFactory(JCRSessionFactory jcrSessionFactory) {
        this.jcrSessionFactory = jcrSessionFactory;
    }

    @Inject
    @GraphQLOsgiService
    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Inject
    @GraphQLOsgiService
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

//    @GraphQLField
//    public String renderModule(
//            @GraphQLName("path") String path,
//            @GraphQLName("view") @GraphQLDescription("Name of the view") String view,
//            @GraphQLName("templateType") @GraphQLDescription("Template type") String templateType,
//            @GraphQLName("contextConfiguration") @GraphQLDescription("Rendering context configuration") String contextConfiguration,
//            DataFetchingEnvironment environment) throws IllegalAccessException, InvocationTargetException, JspException {
//        return renderTag(new ModuleTag(), getAttr(path, view, templateType, contextConfiguration), environment);
//    }

    @GraphQLDescription("Render a compoment according to the parameters")
    @GraphQLField
    public SimpleRenderedNode getRenderedComponent(
            @GraphQLName("mainResourcePath") @GraphQLDescription("Main resource path") String mainResourcePath,
            @GraphQLName("path") @GraphQLDescription("Path of the node") String path,
            @GraphQLName("node") @GraphQLDescription("Input containing the primary node type, the name, the properties and other fields of a JCR Node of a node to render") GqlJcrNodeInput input,
            @GraphQLName("view") @GraphQLDescription("Name of the view") String view,
            @GraphQLName("templateType") @GraphQLDescription("Template type") String templateType,
            @GraphQLName("contextConfiguration") @GraphQLDescription("Rendering context configuration") String contextConfiguration,
            @GraphQLName("isEditMode") @GraphQLDescription("Edit mode") Boolean isEditMode,
            @GraphQLName("language") @GraphQLDescription("Language") String language,
            DataFetchingEnvironment environment
    ) {
        try {
            String id = "cache" + Objects.hash(mainResourcePath, path, view, templateType, input.getName(), input.getPrimaryNodeType(), contextConfiguration, isEditMode, language);
            return new SimpleRenderedNode(id, jcrTemplate.doExecuteWithSystemSessionAsUser(jcrSessionFactory.getCurrentUser(), null, LanguageCodeConverters.languageCodeToLocale(language),
                    session -> getRenderedComponent(mainResourcePath, path, input, view, templateType, contextConfiguration, isEditMode, environment, session)
            ));
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRenderedComponent(String mainResourcePath, String path, GqlJcrNodeInput input, String view, String templateType, String contextConfiguration, Boolean isEditMode, DataFetchingEnvironment environment, JCRSessionWrapper session) throws RepositoryException {
        JCRNodeWrapper main = session.getNode(mainResourcePath);

        JCRNodeWrapper parent = session.getNode(path != null ? path : "/");

        JCRNodeWrapper node = parent.addNode(input.getName() != null ? input.getName() : "temp-node", input.getPrimaryNodeType());
        Collection<GqlJcrPropertyInput> properties = input.getProperties();
        if (properties != null) {
            for (GqlJcrPropertyInput property : properties) {
                node.setProperty(property.getName(), property.getValue());
            }
        }

        if (contextConfiguration == null) {
            contextConfiguration = "module";
        }
        if (templateType == null) {
            templateType = "html";
        }

        Resource r = new Resource(node, templateType, view, contextConfiguration);

        RenderContext renderContext = getRenderContext(environment, main, templateType, contextConfiguration, isEditMode);
        try {
            return renderService.render(r, renderContext);
        } catch (RenderException e) {
            throw new RepositoryException(e);
        }
    }

    private RenderContext getRenderContext(DataFetchingEnvironment environment, JCRNodeWrapper main, String templateType, String contextConfiguration, Boolean isEditMode) throws RepositoryException {
        HttpServletRequest request = ContextUtil.getHttpServletRequest(environment.getContext());
        HttpServletResponse response = ContextUtil.getHttpServletResponse(environment.getContext());
        if (request == null || response == null) {
            throw new RuntimeException("No HttpRequest or HttpResponse");
        }

        if (request instanceof HttpServletRequestWrapper) {
            request = (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest();
        }

        RenderContext renderContext = new RenderContext(request, response, JCRSessionFactory.getInstance().getCurrentUser());
        renderContext.setServletPath(Render.getRenderServletPath());
        renderContext.setSite(main.getResolveSite());
        renderContext.setMainResource(new Resource(main, templateType, null, contextConfiguration));
        if (isEditMode != null) {
            renderContext.setEditMode(isEditMode);
            if (isEditMode) {
                renderContext.setEditModeConfig((EditConfiguration) SpringContextSingleton.getBean("editmode"));
            }
        }
        return renderContext;
    }

    @GraphQLDescription("Rendering result for a node")
    public static class SimpleRenderedNode {
        private final String id;
        private final String output;

        public SimpleRenderedNode(String id, String output) {
            this.id = id;
            this.output = output;
        }

        @GraphQLField
        @GraphQLDescription("Id of of the rendering node")
        public String getId() {
            return id;
        }

        @GraphQLField
        @GraphQLDescription("Rendering output")
        public String getOutput() {
            return output;
        }
    }
}
