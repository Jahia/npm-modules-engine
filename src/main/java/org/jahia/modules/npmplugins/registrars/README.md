## Registrars

The role of registrars is to parse NPM plugins to detect and register specific extensions, written in JS (or anything that require GraalVM engine).
Registrars are called when a NPM plugin is started, after the execution of the initialization script.

Usually, extensions will be installed in the registry by the NPM plugin during this initialization phase. This can bve done 
by calling the registry `add` method, in the init script. The registry is available as java helper in `jahiaHelpers.registry` global variable.

### Render filters

The render filter registrar goal is to register "render filter" written in JS, taken from the registry with the `render-filter` type. 
It works by creating an OSGi service delegating to the JS `prepare` / `execute` functions.

Render filter can be added in the registry, in the form of a pair of 2 methods `execute` and `prepare` :

```javascript
registry.add("render-filter", "test", renderFilterTest, {
    target: 'render:50',
    applyOnNodeTypes: 'jnt:bigText',

    prepare: (renderContext, resource, chain) => {
        
    },
    execute: (previousOut, renderContext, resource, chain) => {
        return previousOut.replace('toto', 'tutu');
    }
})
```

The target must be `render`, followed by the filter priority.

### Views

Views should be registered through a registrar, instead of being handled directly by `jsengine`. See [views](../views/README.md) for details on how JS-based views are registered.

