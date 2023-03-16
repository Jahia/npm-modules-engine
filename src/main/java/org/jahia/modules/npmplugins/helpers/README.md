## Helpers

The java helpers provide the main entry point for JS to java interoperability. 
Helpers are instantiated when a new context is created. Every context has a different instance of the helpers, 
meaning no data are shared between them. These helpers can safely use the JS context to execute JS callbacks.

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

## Using helpers

Helpers are available in the `jahiaHelpers` global variable.

When working with webpack, it can be handy to declare these helpers as "externals" :
```js
externals: {
  '@jahia/server-helpers': 'jahiaHelpers'
}
```

So that they can be used afterwards as standard imports :
```javascript
import {registry} from '@jahia/server-helpers';
```

Completion is provided by adding typing packages for `@jahia/server-helpers` [TBD in BACKLOG-15768]
