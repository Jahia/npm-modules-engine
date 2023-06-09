<!--
    Template for Readmes, see alternatives/examples here: https://github.com/matiassingers/awesome-readme
-->

<!--
    Badges provides a quick glance at the state of the repository and pointers to external resources.
    More can be generated from here: https://shields.io/
-->

<a href="https://www.jahia.com/">
    <img src="https://www.jahia.com/modules/jahiacom-templates/images/jahia-3x.png" alt="Jahia logo" title="Jahia" align="right" height="60" />
</a>

NPM Modules Engine
======================

<p align="center">A module that provide support for NPM-style modules deployment in Jahia</p>

<p align="left">This modules adds support in Jahia for NPM-styled modules, providing server-side extensions written in Javascript. 
Based on GraalVM, it must be run inside a GraalVM environment in order to support Javascript language. NPM modules can contain templates, 
definitions, filter, and other jahia extensions.
</p>

## GraalVM engine

The GraalVM engine provides context to execute Javascript code. More details on the engine features and configuration [here](./src/main/java/org/jahia/modules/npm/modules/engine/jsengine/README.md).

### Java helpers

NPM module comes with a list of Java helpers, available from javascript code. All helpers are
described [here](./src/main/java/org/jahia/modules/npm/modules/engine/helpers/README.md)

## NPM Modules

### Content

A NPM module can contain views, definitions, and other standard jahia extensions.

How to include views in a module is
described [here](./src/main/java/org/jahia/modules/npm/modules/engine/views/README.md).

You can find a reference documentation of all the available out of the box Handle bar
helpers [here](./src/main/java/org/jahia/modules/npm/modules/engine/views/handlebars.md)

Definitions file (`definitions.cnd`) and import (`import.xml`) are placed directly in the root folder of the module.
They follow the same format as in a standard jahia module.

Other extensions are supported with the help
of [registrars](./src/main/java/org/jahia/modules/npm/modules/engine/registrars/README.md).

### Deployment

NPM modules must be packaged with `npm pack` and must contain a `package.json` file, containing all package information.
The `package.json` file must contain a `jahia` section with a `server` entry. This entry points to the initialization
script of the module, which will be responsible of registering the extensions.

```json
{
  ...
  "jahia": {
    "server": "dist/main.js"
  },
  ...
}
```

See [here](./src/main/java/org/jahia/modules/npm/modules/engine/jsengine/README.md#module-registration) for more details on the whole deployment process. 

In order to be parsed as NPM modules, the URL of the module must be wrapped with `npm://` protocol - for example, `npm://file:///tmp/module-1.0.0.tgz`. 
These URLs can be used within karaf console or provisioning API to install them.
See [here](./src/main/java/org/jahia/modules/npm/modules/engine/npmhandler/README.md) to see how this protocol is handled.

Modules can also be deployed as other modules, through REST api, Module management UI, or fileinstall folder, provided that they use the `tgz` extension :

```shell
curl -s --user root:root1234 --form bundle=@npm-modules-example.tgz --form start=true http://localhost:8080/modules/api/bundles
```

## Open-Source

This is an Open-Source module, you can find more details about Open-Source @ Jahia [in this repository](https://github.com/Jahia/open-source)
