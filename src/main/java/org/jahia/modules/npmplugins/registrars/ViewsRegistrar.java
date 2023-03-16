package org.jahia.modules.npmplugins.registrars;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npmplugins.helpers.Registry;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.modules.npmplugins.views.JSScript;
import org.jahia.modules.npmplugins.views.JSView;
import org.jahia.modules.npmplugins.views.ViewParser;
import org.jahia.modules.npmplugins.views.hbs.HandlebarsParser;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.*;
import org.jahia.services.render.scripting.Script;
import org.jahia.services.render.scripting.ScriptResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
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
    private Map<Bundle, Collection<JSView>> viewsPerBundle = new HashMap<>();

    @Reference
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Reference
    public void setGraalVMEngine(GraalVMEngine graalVMEngine) {
        this.graalVMEngine = graalVMEngine;
    }

    @Activate
    public void activate(BundleContext context) {
        List<ScriptResolver> l = new ArrayList<>(renderService.getScriptResolvers());
        l.add(0, this);
        renderService.setScriptResolvers(l);

        parsers = Arrays.asList(new HandlebarsParser());
    }

    @Deactivate
    public void deactivate(BundleContext context) {
        List<ScriptResolver> l = new ArrayList<>(renderService.getScriptResolvers());
        l.remove(this);
        renderService.setScriptResolvers(l);
    }

    @Override
    public void register(Registry registry, Bundle bundle, GraalVMEngine engine) {
        Set<JSView> views = new HashSet<>();

        views.addAll(parseBundleFolder(bundle));
        views.addAll(getRegistryViewsSet(registry, bundle));

        viewsPerBundle.put(bundle, views);
    }

    @Override
    public void unregister(Registry registry, Bundle bundle) {
        viewsPerBundle.remove(bundle);
    }

    private Collection<JSView> getRegistryViewsSet(Registry registry, Bundle bundle) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", "view");
        filter.put("bundle", Value.asValue(bundle));
        return registry.find(filter).stream().map(JSView::new).collect(Collectors.toSet());
    };

    private Collection<JSView> parseBundleFolder(Bundle bundle) {
        Enumeration<String> nodeTypesPaths = bundle.getEntryPaths("views");
        if (nodeTypesPaths != null) {
            Set<JSView> views = new HashSet<>();

            while (nodeTypesPaths.hasMoreElements()) {
                String nodeTypePath = nodeTypesPaths.nextElement();

                Enumeration<String> templateTypePaths = bundle.getEntryPaths(nodeTypePath);
                if (templateTypePaths != null) {
                    while (templateTypePaths.hasMoreElements()) {
                        String templateTypePath = templateTypePaths.nextElement();

                        Enumeration<String> viewPaths = bundle.getEntryPaths(templateTypePath);
                        if (viewPaths != null) {
                            while (viewPaths.hasMoreElements()) {
                                String viewPath = viewPaths.nextElement();
                                for (ViewParser parser : parsers) {
                                    if (parser.canHandle(viewPath)) {
                                        JSView view = parser.parseView(bundle, viewPath);
                                        if (view != null) {
                                            views.add(view);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return views;
        }

        return Collections.emptySet();
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
        return (JSView) nodeTypeList.stream().flatMap(t -> getViewsSet(t, renderContext.getSite(), resource.getTemplateType()).stream())
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
