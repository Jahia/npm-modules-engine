package org.jahia.modules.npmplugins.views;

import org.apache.commons.lang.StringUtils;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.modules.npmplugins.registrars.Registrar;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.*;
import org.jahia.services.render.scripting.Script;
import org.jahia.services.render.scripting.ScriptResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(immediate = true, service = {ScriptResolver.class, Registrar.class})
public class JSScriptResolver implements ScriptResolver, BundleListener {
    private static final Logger logger = LoggerFactory.getLogger(JSScriptResolver.class);

    private RenderService renderService;
    private GraalVMEngine graalVMEngine;

    private Map<Bundle, Collection<JSView>> autoDetectedViews = new HashMap<>();

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

        for (Bundle bundle : context.getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                enableBundle(bundle);
            }
        }
        context.addBundleListener(this);

    }

    @Deactivate
    public void deactivate(BundleContext context) {
        List<ScriptResolver> l = new ArrayList<>(renderService.getScriptResolvers());
        l.remove(this);
        renderService.setScriptResolvers(l);

        context.removeBundleListener(this);

        for (Bundle bundle : context.getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                disableBundle(bundle);
            }
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        try {
            Bundle bundle = event.getBundle();
            if (event.getType() == BundleEvent.STARTED) {
                enableBundle(bundle);
            } else if (event.getType() == BundleEvent.STOPPED) {
                disableBundle(bundle);
            }
        } catch (Exception e) {
            logger.error("Cannot handle event", e);
        }
    }

    public void enableBundle(Bundle bundle) {
        URL packagejson = bundle.getResource("package.json");
        if (packagejson != null) {
            parseBundeFolder(bundle);
        }
    }

    private void parseBundeFolder(Bundle bundle) {
        Enumeration<String> nodeTypesPaths = bundle.getEntryPaths("views");
        if (nodeTypesPaths != null) {
            Set<JSView> views = new TreeSet<>();

            while (nodeTypesPaths.hasMoreElements()) {
                String nodeTypePath = nodeTypesPaths.nextElement();

                Enumeration<String> templateTypePaths = bundle.getEntryPaths(nodeTypePath);
                while (templateTypePaths.hasMoreElements()) {
                    String templateTypePath = templateTypePaths.nextElement();

                    Enumeration<String> viewPaths = bundle.getEntryPaths(templateTypePath);
                    while (viewPaths.hasMoreElements()) {
                        String viewPath = viewPaths.nextElement();
                        JSView view = parseView(bundle, viewPath);
                        if (view != null) {
                            views.add(view);
                        }
                    }
                }
            }
            if (!views.isEmpty()) {
                autoDetectedViews.put(bundle, views);
            }
        }
    }

    private JSFileView parseView(Bundle bundle, String viewPath) {
        if (viewPath.endsWith(".hbs")) {
            String[] parts = StringUtils.split(viewPath, "/");
            String[] viewNameParts = StringUtils.split(StringUtils.substringAfterLast(viewPath, "/"), ".");
            JahiaTemplatesPackage module = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getTemplatePackageById(bundle.getSymbolicName());

            //todo: bad replace from _ to :
            JSFileView fileView = new JSFileView("handlebars", viewPath, viewNameParts.length > 2 ? viewNameParts[1] : "default", module, parts[1].replace('_', ':'), parts[2]);
            fileView.setProperties(new Properties());

            URL props = bundle.getResource(StringUtils.substringBeforeLast(viewPath, ".hbs") + ".properties");
            if (props != null) {
                try (InputStream inStream = props.openStream()) {
                    fileView.getProperties().load(inStream);
                } catch (IOException e) {
                    logger.error("Cannot read", e);
                }
            }

            fileView.setDefaultProperties(new Properties());
            return fileView;
        }
        return null;
    }

    public void disableBundle(Bundle bundle) {
        autoDetectedViews.remove(bundle);
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
        List<ExtendedNodeType> nodeTypeList = new LinkedList<ExtendedNodeType>();
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
                .filter(v -> v.getKey().equals(resource.getTemplate()))
                .findFirst()
                .orElseThrow(() -> new TemplateNotFoundException(resource.getTemplate()));
    }


    @Override
    public boolean hasView(ExtendedNodeType nt, String viewName, JCRSiteNode site, String templateType) {
        return getViewsSet(nt, site, templateType).stream().anyMatch(v -> v.getKey().equals(viewName));
    }

    @Override
    public SortedSet<View> getViewsSet(ExtendedNodeType extendedNodeType, JCRSiteNode jcrSiteNode, String templateType) {
        Set<String> modulesWithAllDependencies = jcrSiteNode.getInstalledModulesWithAllDependencies();
        return Stream.concat(getRegistryViewsSet(), getFilesViewsSet())
                .filter(v -> modulesWithAllDependencies.contains(v.getModule().getId()))
                .filter(v -> templateType.equals(v.getTemplateType()))
                .filter(v -> extendedNodeType.isNodeType(v.getTarget()))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private Stream<JSView> getFilesViewsSet() {
        return autoDetectedViews.values().stream()
                .flatMap(Collection::stream);
    }

    private Stream<JSView> getRegistryViewsSet() {
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", "view");
        return graalVMEngine.doWithContext(contextProvider -> {
            return contextProvider.getRegistry().find(filter).stream()
                    .filter(p -> p.containsKey("templateType"))
                    .filter(p -> p.containsKey("target"))
                    .map(JSView::new);
        });
    }
}
