package org.jahia.modules.npmplugins.registrars;

import org.jahia.modules.npmplugins.helpers.RegistryHelper;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Registrar.class, immediate = true,  configurationPolicy= ConfigurationPolicy.OPTIONAL)
public class GraphqlRegistrar implements Registrar {

    @Override
    public void register(RegistryHelper registry, Bundle bundle) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", "graphql-resolver");
        filter.put("bundle", bundle);

        List<Map<String, Object>> graphqlResolvers = registry.getRegistry().find(filter);
        for (Map<String, Object> resolver : graphqlResolvers) {
            // Register
        }
    }

    @Override
    public void unregister(RegistryHelper registry, Bundle bundle) {
        // Unregister
    }
}
