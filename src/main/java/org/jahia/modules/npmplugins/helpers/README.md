## Helpers

Helpers are available in the `jahiaHelpers` global variable. Every context has a different version of the helpers, 
meaning no data are shared between them.

### Registry

Registry helper is a java implementation of the registry from `@jahia/ui-extender`. It's mainly used to register extensions from the initialization scripts,
which can then be read from the java code. 

Since data are not shared between context, it cannot be used to share data, and we should not write into it outside of init scripts.

Todo: It should be ok and simpler to use the JS implementation of the registry

### GQLHelper

This helper provides a bridge to the GraphQL API, by mocking request/responses and calling the graphql servlet.

### RenderHelper

The RenderHelper provide access to the render service, and an equivalent to the jahia:module tags. 
Note that the same can be achieved with the Graphql API, but accessing directly the render service might be more flexible in some cases.

### OSGiHelper

Provide access to OSGi bundles.

### NodeHelper

The NodeHelper gives an JS like access to JCR nodes.
