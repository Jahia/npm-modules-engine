package org.jahia.modules.npm.modules.engine.helpers;

import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.JSGlobalVariableFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component(service = {JSGlobalVariableFactory.class, CoreHelperFactory.class}, immediate = true)
public class CoreHelperFactory implements JSGlobalVariableFactory {
    private static final Logger logger = LoggerFactory.getLogger(CoreHelperFactory.class);

    @Override
    public String getName() {
        return "jahiaHelpers";
    }

    @Override
    public Object getObject(ContextProvider contextProvider) {
        Map<String,Object> helpers = new HashMap<>();
        helpers.put("registry", new RegistryHelper(contextProvider));
        helpers.put("render", new RenderHelper(contextProvider));
        helpers.put("gql", new GQLHelper(contextProvider));
        helpers.put("osgi", new OSGiHelper(contextProvider));
        helpers.put("node", new NodeHelper(contextProvider));

        for (Map.Entry<String, Object> entry : helpers.entrySet()) {
            try {
                OSGiServiceInjector.handleMethodInjection(entry.getValue());
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Cannot inject services for {} helper", entry.getKey(), e);
            }
        }

        contextProvider.getHelpers().putAll(helpers);

        return ProxyObject.fromMap(helpers);
    }
}
