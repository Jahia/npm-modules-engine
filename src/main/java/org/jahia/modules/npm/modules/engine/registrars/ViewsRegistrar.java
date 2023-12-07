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

import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.modules.npm.modules.engine.views.JSScript;
import org.jahia.modules.npm.modules.engine.views.JSView;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.observation.JahiaEventListener;
import org.jahia.services.render.*;
import org.jahia.services.render.scripting.Script;
import org.jahia.services.render.scripting.ScriptResolver;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.*;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(immediate = true, service = {Registrar.class, ScriptResolver.class, JahiaEventListener.class})
public class ViewsRegistrar implements ScriptResolver, Registrar, JahiaEventListener<EventObject> {

    private RenderService renderService;
    private GraalVMEngine graalVMEngine;

    private final Map<Bundle, Collection<JSView>> viewsPerBundle = new HashMap<>();

    private static final Map<String, SortedSet<View>> viewSetCache = new ConcurrentHashMap<>(512);

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

        graalVMEngine.doWithContext(contextProvider -> {
            views.addAll(getRegistryViewsSet(bundle, contextProvider));
        });

        viewsPerBundle.put(bundle, views);
        clearCache();
    }

    @Override
    public void unregister(Bundle bundle) {
        viewsPerBundle.remove(bundle);
        clearCache();
    }

    private Collection<JSView> getRegistryViewsSet(Bundle bundle, ContextProvider contextProvider) {
        JahiaTemplatesPackage module = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getTemplatePackageById(bundle.getSymbolicName());
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", "view");
        filter.put("bundleKey", bundle.getSymbolicName());
        return contextProvider.getRegistry()
                .find(filter)
                .stream()
                .map(registryEntry -> new JSView(registryEntry, module))
                .collect(Collectors.toSet());
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
                .filter(v -> v.getKey().equals(resource.getResolvedTemplate()) ||
                        ((resource.getResolvedTemplate() == null || "default".equals(resource.getResolvedTemplate())) && "page".equals(resource.getContextConfiguration()) && v.getProperties().containsKey("applyOn")))
                .findFirst()
                .orElseThrow(() -> new TemplateNotFoundException(resource.getResolvedTemplate()));
    }


    @Override
    public boolean hasView(ExtendedNodeType nt, String viewName, JCRSiteNode site, String templateType) {
        return getViewsSet(nt, site, templateType).stream().anyMatch(v -> v.getKey().equals(viewName));
    }

    @Override
    public SortedSet<View> getViewsSet(ExtendedNodeType extendedNodeType, JCRSiteNode jcrSiteNode, String templateType) {
        final String cacheKey = extendedNodeType.getName() + "_" +
                "_" + (jcrSiteNode != null ? jcrSiteNode.getPath() : "") + "__" +
                templateType + "_";

        if (viewSetCache.containsKey(cacheKey)) {
            return viewSetCache.get(cacheKey);
        }

        Set<String> modulesWithAllDependencies = jcrSiteNode.getInstalledModulesWithAllDependencies();
        SortedSet<View> viewsSet = getFilesViewsSet()
                .filter(v -> modulesWithAllDependencies.contains(v.getModule().getId()))
                .filter(v -> templateType.equals(v.getTemplateType()))
                .filter(v -> extendedNodeType.isNodeType(v.getNodeType()) ||
                        (v.getProperties().containsKey("applyOn") &&
                                Arrays.stream(v.getProperties().getProperty("applyOn").split(",")).anyMatch(nodeType -> extendedNodeType.isNodeType(nodeType))))
                .collect(Collectors.toCollection(TreeSet::new));
        viewSetCache.put(cacheKey, viewsSet);
        return viewsSet;
    }

    private Stream<JSView> getFilesViewsSet() {
        return viewsPerBundle.values().stream()
                .flatMap(Collection::stream);
    }

    private void clearCache() {
        viewSetCache.clear();
    }

    @Override
    public void onEvent(EventObject eventObject) {
        if (eventObject instanceof JahiaTemplateManagerService.TemplatePackageRedeployedEvent
                || eventObject instanceof JahiaTemplateManagerService.ModuleDeployedOnSiteEvent
                || eventObject instanceof JahiaTemplateManagerService.ModuleDependenciesEvent) {
            clearCache();
        }
    }
}
