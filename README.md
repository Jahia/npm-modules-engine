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

## GraalVM engine

The GraalVM engine provides context to execute Javascript code. More details on the engine features and configuration [here](./src/main/java/org/jahia/modules/npmplugins/jsengine/README.md).

### Java helpers

NPM module comes with a list of Java helpers, available from javascript code. All helpers are described [here](./src/main/java/org/jahia/modules/npmplugins/helpers/README.md)

## NPM plugins

### Content

A NPM plugin module can contain views, definitions, and other standard jahia extensions. 

How to include views in a module is described [here](./src/main/java/org/jahia/modules/npmplugins/views/README.md).

Definitions file (`definitions.cnd`) and import (`import.xml`) are placed directly in the root folder of the module.
They follow the same format as in a standard jahia module.

Other extensions are supported with the help of [registrars](./src/main/java/org/jahia/modules/npmplugins/registrars/README.md).

### Deployment

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

See [here](./src/main/java/org/jahia/modules/npmplugins/jsengine/README.md#module-registration) for more details on the whole deployment process. 

In order to be parsed as NPM modules, the URL of the module must be wrapped with `npm://` protocol - for example, `npm://file:///tmp/module-1.0.0.tgz`. 
These URLs can be used within karaf console or provisioning API to install them.
See [here](./src/main/java/org/jahia/modules/npmplugins/npmhandler/README.md) to see how this protocol is handled.

Modules can also be deployed as other modules, through REST api, Module management UI, or fileinstall folder, provided that they use the `tgz` extension :

```shell
curl -s --user root:root1234 --form bundle=@npm-plugin-example.tgz --form start=true http://localhost:8080/modules/api/bundles
```

