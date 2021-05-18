<!--
    Template for Readmes, see alternatives/examples here: https://github.com/matiassingers/awesome-readme
-->

<!--
    Badges provides a quick glance at the state of the repository and pointers to external resources.
    More can be generated from here: https://shields.io/
-->

|           |                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| --------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Module    | ![ID](https://img.shields.io/badge/ID-npm--plugins-blue) ![Store](https://img.shields.io/badge/Jahia%20Store-No-red) |
| CI / CD   | [![CircleCI](https://circleci.com/gh/Jahia/npm-plugins/tree/main.svg?style=shield)](https://app.circleci.com/pipelines/github/Jahia/npm-plugins) |
| Artifacts | [![Snapshot](https://img.shields.io/badge/Snapshot-Nexus-blue)](https://devtools.jahia.com/nexus/content/repositories/jahia-snapshots/org/jahia/modules/npm-plugins/) [![Release](https://img.shields.io/badge/Release-Nexus-blue)](https://devtools.jahia.com/nexus/content/repositories/jahia-releases/org/jahia/modules/npm-plugins/) |

# NPM Plugins

<p align="center">A module that provide support for NPM-style modules deployment in Jahia</p>

<p align="left">This modules adds support in Jahia for NPM-styled modules, providing server-side extensions written in Javascript. 
Based on GraalVM, it must be run inside a GraalVM environment in order to support Javascript language. NPM modules can contain templates, 
definitions, filter, and other jahia extensions.
</p>

## Table of content

- [Presentation](#presentation)
- [GraalVM engine](#graalvm-engine)
- [Module deployment](#module-deployment)
- [Registering extensions](#registering-extensions)
- [Views](#views)

## Presentation

## GraalVM engine

### JS Context pooling

As Javascript engine is not multi-threaded, it's not possible for different threads to use the same JS context in parallel. 
Instead, a pool of JS contexts can be used when a thread need to do JS calls. Each context is initialized with the same scripts, and should contain the same values. 

### Java helpers

NPM module comes with a list of Java helpers, available from javascript code. These helpers are provided by [`CoreHelperFactory`](./src/main/java/org/jahia/modules/npmplugins/helpers/CoreHelperFactory.java)
Any module can add new helpers by exposing an OSGi service implementing [`JSGlobalVariableFactory`](./src/main/java/org/jahia/modules/npmplugins/jsengine/JSGlobalVariableFactory.java).

All helpers are available inside a global `jahiaHelpers` object in Javascript context. 
Java helpers are also per JS context. These helpers can safely use the JS context they are bound to execute JS callbacks.

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

## Module deployment

NPM modules must be packaged with `npm pack` and must contain a `package.json` file, containing all package information. 
The `package.json` file must contain a `jahia` section with a `server` entry. This entry points to the initialization script of the module, which will be responsible of registering the extensions.

```json
{
  ...
  "jahia": {
    "server": "dist/main.js"
  },
  ...
}
```

In order to be parsed as NPM modules, the URL of the module must be wrapped with `npm://` protocol - for example, `npm://file:///tmp/module-1.0.0.tgz`. 
These URLs can be used within karaf console or provisioning API to install them.
The npm module will be internally converted to an OSGi module with a pax [URL handler](./src/main/java/org/jahia/modules/npmplugins/npmhandler/NpmProtocolStreamHandler.java) 

Modules can also be deployed as other modules, through REST api, Module management UI, or fileinstall folder, provided that they use the `tgz` extension :

```shell
curl -s --user root:root1234 --form bundle=@npm-plugin-example.tgz --form start=true http://localhost:8080/modules/api/bundles
```

Format detection is handled by a specific [transformer](./src/main/java/org/jahia/modules/npmplugins/npmhandler/FileinstallTgzTransformer.java) which will wraps the URL with `npm://` protocol.

### Module lifecycle

When a module is started, its initialization script is added to the list of known init scripts. All registered initialization scripts are executed when a new Javascript context is created.
If a module is stopped, its script is unregistered. Any addition or removal of an initialization script invalidates the existing JS contexts, which will be closed and recreated.

The goal of these initialization scripts is to initialize the Javascript context just after their creation, not to execute code on module start. The scripts will be called everytime a Javascript context needs to be created.

## Registering extensions

All extensions need to be registered by calling the registry `add` method, in the init script. The registry is available as java helper in `jahiaHelpers.registry` global variable.

### Views

In npm-plugins views are JS functions, which takes "resource" and "render context" parameters (as java objects), and returns a string. In this context, "view" and "template" terms are equivalent. Resource contains the node, the optional view name to use, and some other rendering parameters. Render context is the global context for the request. Views are registered in the registry as other ui extensions, in module initialization :

```javascript
import {registry} from '@jahia/server-helpers';

registry.add("view", "page-simple", {
  target: 'jnt:page',
  templateName: 'simple',
  templateType: 'html',
  displayName: "Simple template",
  properties: {
    template: 'true'
  },
  render: (resource,context) => 'toto'
});
```

When it needs to render a node, the render service asks for registered "ScriptResolver" implementation - NPM plugins provide an implementation [`JSScriptResolver.java`](./src/main/java/org/jahia/modules/npmplugins/views/JSScriptResolver.java) which is looking into the registry to get these view. 
The implementation is currently simple and not really optimized, but it basically looks into the registry for any view that matches the criteria. `JSView` contains the view metadata, and `JSScript` the rendering part. It simply dispatches the resource/context to the JS function, and return the generated result. 

A complex JS view example can be found [here](https://github.com/Jahia/npm-plugin-example/blob/8e111d7303a81a48dd064e1c75a1a31797e4d126/src/views/test/test.js) - it has been removed since as all views have been replaced with handlebars, but can still be interesting for review. Using pure JS view is not something people will do for basic views, but are the base for implementing any js templating language (as handlebars).

Views can be implemented in handlebars, documentation [here](./handlebars.md)

### Views as templates

In npm-pluins, templates are only views - there's no need to create a template in the studio anymore. Page templates will be created as views for the `jnt:page` type - the `jnt:template` type is not used at all.
These views need to declare `template: 'true'` in their properties. The `SimpleTemplateNodeFilter` will detect that and change the behaviour of the standard `TemplateNodeFilter` when this property is detected. 

### Render filters

### Other filesImport and definitions

Definitions file (`definitions.cnd`) and import (`import.xml`) are placed directly in the root folder of the module.
They follow the same format as in a standard jahia module.

