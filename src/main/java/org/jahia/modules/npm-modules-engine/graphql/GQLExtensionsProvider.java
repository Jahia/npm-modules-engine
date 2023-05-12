package org.jahia.modules.npm-modules-engine.graphql;

import org.jahia.modules.graphql.provider.dxm.DXGraphQLExtensionsProvider;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service= DXGraphQLExtensionsProvider.class)
public class GQLExtensionsProvider implements DXGraphQLExtensionsProvider {
}
