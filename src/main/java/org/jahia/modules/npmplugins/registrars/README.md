## Registrars

The role of registrars is to parse NPM plugins to detect and register specific extensions, written in JS (or anything that require GraalVM engine).
Registrars are called when a NPM plugin is started, after the execution of the initialization script.
Usually, extensions will be installed in the registry by the NPM plugin during this initialization phase. 

### Render filters

The render filter registrar goal is to register "render filter" written in JS, taken from the registry with the `render-filter` type. 
It works by creating an OSGi service delegating to the JS `prepare` / `execute` functions.

### Views

Views should be registered through a registrar, instead of being handled directly by `jsengine`. See [views](../views/README.md) for details on how JS-based views are registered.

