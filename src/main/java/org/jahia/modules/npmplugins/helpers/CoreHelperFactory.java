package org.jahia.modules.npmplugins.helpers;

import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.JSGlobalVariableFactory;
import org.osgi.service.component.annotations.Component;

import java.util.HashMap;
import java.util.Map;

@Component(service = {JSGlobalVariableFactory.class, CoreHelperFactory.class}, immediate = true)
public class CoreHelperFactory implements JSGlobalVariableFactory {

    @Override
    public Map<String,Object> getHelperInstances(ContextProvider context) {
        Map<String,Object> m = new HashMap<>();
        m.put("registry", new RegistryHelper(context));
        m.put("render", new RenderHelper(context));
        m.put("gql", new GQLHelper(context));
        m.put("osgi", new OSGiHelper(context));
        m.put("node", new NodeHelper(context));
        return m;
    }
}
