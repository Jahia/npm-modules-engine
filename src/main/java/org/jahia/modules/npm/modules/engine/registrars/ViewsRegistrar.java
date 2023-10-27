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
package org.jahia.modules.npm.modules.engine.registrars;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.modules.npm.modules.engine.views.JSScript;
import org.jahia.modules.npm.modules.engine.views.JSView;
import org.jahia.modules.npm.modules.engine.views.ViewParser;
import org.jahia.modules.npm.modules.engine.views.hbs.HandlebarsParser;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.*;
import org.jahia.services.render.scripting.Script;
import org.jahia.services.render.scripting.ScriptResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(immediate = true, service = {Registrar.class, ScriptResolver.class})
public class ViewsRegistrar implements ScriptResolver, Registrar {
    private static final Logger logger = LoggerFactory.getLogger(ViewsRegistrar.class);

    private RenderService renderService;
    private GraalVMEngine graalVMEngine;

    private List<ViewParser> parsers;
    private final Map<Bundle, Collection<JSView>> viewsPerBundle = new HashMap<>();

    @Reference
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setGraalVMEngine(GraalVMEngine graalVMEngine) {
        this.graalVMEngine = graalVMEngine;
    }

    @Activate
    public void activate(BundleContext context) {
        List<ScriptResolver> l = new ArrayList<>(renderService.getScriptResolvers());
        l.add(0, this);
        renderService.setScriptResolvers(l);

        parsers = List.of(new HandlebarsParser());
    }

    @Deactivate
    public void deactivate(BundleContext context) {
        List<ScriptResolver> l = new ArrayList<>(renderService.getScriptResolvers());
        l.remove(this);
        renderService.setScriptResolvers(l);
    }
    @Override
    public void register(Bundle bundle) {
        Set<JSView> views = new HashSet<>();

        views.addAll(parseBundleFolder(bundle));
        views.addAll(getRegistryViewsSet(bundle));

        viewsPerBundle.put(bundle, views);
    }

    @Override
    public void unregister(Bundle bundle) {
        viewsPerBundle.remove(bundle);
    }

    private Collection<JSView> getRegistryViewsSet(Bundle bundle) {
        return graalVMEngine.doWithContext(contextProvider -> {
            Map<String, Object> filter = new HashMap<>();
            filter.put("type", "view");
            filter.put("bundle", Value.asValue(bundle));
            return contextProvider.getRegistry().find(filter).stream().map(JSView::new).collect(Collectors.toSet());
        });
    }

    private Collection<JSView> parseBundleFolder(Bundle bundle) {
        return parseComponentsFolder(bundle);
    }

    private Set<JSView> parseComponentsFolder(Bundle bundle) {
        Enumeration<String> namespaces = bundle.getEntryPaths("components");
        Set<JSView> views = new HashSet<>();
        if (namespaces != null) {
            while (namespaces.hasMoreElements()) {
                String namespace = namespaces.nextElement();
                views.addAll(parseNamespace(bundle, namespace));
            }
        }
        return views;
    }

    private Set<JSView> parseNamespace(Bundle bundle, String namespace) {
        Enumeration<String> componentsName = bundle.getEntryPaths(namespace);
        Set<JSView> views = new HashSet<>();
        while (componentsName.hasMoreElements()) {
            String componentName = componentsName.nextElement();
            Enumeration<String> filePaths = bundle.getEntryPaths(componentName);
            if (filePaths != null) {
                while (filePaths.hasMoreElements()) {
                    String filePath = filePaths.nextElement();
                    parsers.stream().filter(parser -> parser.canHandle(filePath)).findFirst().ifPresent(viewParser -> {
                        JSView view = viewParser.parseView(bundle, filePath);
                        if (view != null) {
                            views.add(view);
                        }
                    });
                }
            }
        }
        return views;
    }

    @Override
    public Script resolveScript(Resource resource, RenderContext renderContext) throws TemplateNotFoundException {
        try {
            return new JSScript(resolveView(resource, renderContext), graalVMEngine);
        } catch (RepositoryException e) {
            throw new TemplateNotFoundException(e);
        }
    }

    private JSView resolveView(Resource resource, RenderContext renderContext) throws RepositoryException, TemplateNotFoundException {
        ExtendedNodeType nt = resource.getNode().getPrimaryNodeType();
        List<ExtendedNodeType> nodeTypeList = getNodeTypeList(nt);
        for (ExtendedNodeType type : resource.getNode().getMixinNodeTypes()) {
            nodeTypeList.addAll(0, type.getSupertypeSet());
            nodeTypeList.add(0, type);
        }

        if (resource.getResourceNodeType() != null) {
            nodeTypeList.addAll(0, getNodeTypeList(resource.getResourceNodeType()));
        }

        return resolveView(resource, nodeTypeList, renderContext);
    }

    private List<ExtendedNodeType> getNodeTypeList(ExtendedNodeType nt) throws NoSuchNodeTypeException {
        List<ExtendedNodeType> nodeTypeList = new LinkedList<>();
        nodeTypeList.add(nt);
        nodeTypeList.addAll(nt.getSupertypeSet());
        ExtendedNodeType base = NodeTypeRegistry.getInstance().getNodeType("nt:base");
        if (nodeTypeList.remove(base)) {
            nodeTypeList.add(base);
        }
        return nodeTypeList;
    }

    private JSView resolveView(Resource resource, List<ExtendedNodeType> nodeTypeList, RenderContext renderContext) throws RepositoryException, TemplateNotFoundException {
        return (JSView) nodeTypeList.stream().flatMap(nodeType -> getViewsSet(nodeType, renderContext.getSite(), resource.getTemplateType()).stream())
                .filter(v -> v.getKey().equals(resource.getResolvedTemplate()))
                .findFirst()
                .orElseThrow(() -> new TemplateNotFoundException(resource.getResolvedTemplate()));
    }


    @Override
    public boolean hasView(ExtendedNodeType nt, String viewName, JCRSiteNode site, String templateType) {
        return getViewsSet(nt, site, templateType).stream().anyMatch(v -> v.getKey().equals(viewName));
    }

    @Override
    public SortedSet<View> getViewsSet(ExtendedNodeType extendedNodeType, JCRSiteNode jcrSiteNode, String templateType) {
        Set<String> modulesWithAllDependencies = jcrSiteNode.getInstalledModulesWithAllDependencies();
        return getFilesViewsSet()
                .filter(v -> modulesWithAllDependencies.contains(v.getModule().getId()))
                .filter(v -> templateType.equals(v.getTemplateType()))
                .filter(v -> extendedNodeType.isNodeType(v.getTarget()))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private Stream<JSView> getFilesViewsSet() {
        return viewsPerBundle.values().stream()
                .flatMap(Collection::stream);
    }

}
