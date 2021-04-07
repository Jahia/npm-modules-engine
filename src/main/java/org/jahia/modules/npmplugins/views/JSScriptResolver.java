package org.jahia.modules.npmplugins.views;

import org.jahia.modules.npmplugins.helpers.CoreHelperFactory;
import org.jahia.modules.npmplugins.helpers.RegistryHelper;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.*;
import org.jahia.services.render.scripting.Script;
import org.jahia.services.render.scripting.ScriptResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import java.util.*;
import java.util.stream.Collectors;

@Component(immediate = true)
public class JSScriptResolver implements ScriptResolver {

    private RenderService renderService;
    private GraalVMEngine graalVMEngine;

    @Reference
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Reference
    public void setGraalVMEngine(GraalVMEngine graalVMEngine) {
        this.graalVMEngine = graalVMEngine;
    }

    @Activate
    public void activate() {
        List<ScriptResolver> l = new ArrayList<>(renderService.getScriptResolvers());
        l.add(0, this);
        renderService.setScriptResolvers(l);
    }

    @Deactivate
    public void deactivate() {
        List<ScriptResolver> l = new ArrayList<>(renderService.getScriptResolvers());
        l.remove(this);
        renderService.setScriptResolvers(l);
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
    public boolean hasView(ExtendedNodeType nt, String viewName, JCRSiteNode site,  String templateType) {
        return getViewsSet(nt, site, templateType).stream().anyMatch(v -> v.getKey().equals(viewName));
    }

    @Override
    public SortedSet<View> getViewsSet(ExtendedNodeType extendedNodeType, JCRSiteNode jcrSiteNode, String templateType) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("target", extendedNodeType.getName());
        filter.put("templateType", templateType);
        return graalVMEngine.doWithContext(contextProvider -> {
            return contextProvider.getRegistry().find(filter).stream().map(JSView::new).collect(Collectors.toCollection(TreeSet::new));
        });
    }
}
