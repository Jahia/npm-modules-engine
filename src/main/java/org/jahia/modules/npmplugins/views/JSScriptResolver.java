package org.jahia.modules.npmplugins.views;

import org.jahia.modules.npmplugins.helpers.RegistryHelper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.render.*;
import org.jahia.services.render.scripting.Script;
import org.jahia.services.render.scripting.ScriptResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;

@Component(immediate = true)
public class JSScriptResolver implements ScriptResolver {

    private RenderService renderService;
    private RegistryHelper registryHelper;

    @Reference
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Reference
    public void setRegistryHelper(RegistryHelper registryHelper) {
        this.registryHelper = registryHelper;
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
            return new JSScript(resolveView(resource, renderContext));
        } catch (RepositoryException e) {
            throw new TemplateNotFoundException(e);
        }
    }

    private JSView resolveView(Resource resource, RenderContext renderContext) throws RepositoryException, TemplateNotFoundException {
        return (JSView) getViewsSet(resource.getNode().getPrimaryNodeType(), renderContext.getSite(), resource.getTemplateType()).stream()
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
        return registryHelper.getRegistry().find(filter).stream().map(JSView::new).collect(Collectors.toCollection(TreeSet::new));
    }
}
