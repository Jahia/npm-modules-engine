## GraalVM engine

This contains the core part of the NPM plugin module execution.

The GraalVM engine will create a pool of polyglot context, that can be used to execute JS or any GraalVM supported language.

### Engine and context pool

The service is composed of shared GraalVM engine, and a pool of JS polyglot contexts. 
Javascript code can be executed in a polyglot context.

Polyglot context are not thread safe, and should be used by one single thread at a time. We provide a pool of `ContextProvider`, which contains a polyglot context, allowing multiple threads to execute javascript in different contexts.

Everytime a new context is created, we bind a global variables from java objects, created by instances of `JSGlobalVariableFactory`. 

We then execute a list of initialization scripts. The main script is coming from [`src/javascript/index.js`](../../../../../../../javascript/README.md), its goal is to initialize the available frameworks and provide some polyfills. 
Every NPM plugin will also provide its own initialization script, which will be executed on every context creation.
Whenever a new script is added or removed, we increment a local version number that will tell us older contexts are outdated and should be removed from the pool. 
New contexts will be created with the new scripts list.

### Configuration

The engine can be configured with the `org.jahia.modules.npmplugins.jsengine.GraalVMEngine.cfg` configuration file. All keys prefixed by `polyglot.` will be passed as options to the engine builder.

`experimental` enable experimental features on the engine

`polyglot.js.*` are options related to the Javascript engine.

`polyglot.inspect` enable the javascript debugger.

All available options can checked with `polyglot --help:all`

### Helpers

When creating a new context, multiple helpers are provided in the `jahiaHelpers` global variable, by [`CoreHelperFactory`](../helpers/CoreHelperFactory.java). These helpers are java services, which can be used anywhere in the javascript code as a standard javascript service. 
They are also directly available in the `ContextProvider` class (mainly to get the [RegistryHelper](../helpers/README.md#registry) from the [Registrars](../registrars/README.md))

More details on available helpers [here](../helpers/README.md).

### Module registration and initialization scripts

A bundle listener ([`NpmModuleListener`](../NpmModuleListener.java)) listen to the starting and stopping bundles. 

When a bundle is started, we goes into the following flow :

- Check if we have a `package.json` at root level, and if it contains `jahia.server` entry. If it does, this is a NPM plugin. The `jahia.server` entry gives the path to the initialization script of the module. All NPM plugins must a have an initialization script.
- This script must be a single executable js file, compiled with webpack.
- The init script is added to the list of scripts to be executed when creating a new context. 
- Version number is incremented to invalidate the existing contexts in the pool
- We take a new context from the pool and call all available registrars with it. 
- Registrars will register Jahia extensions by transforming JS object put in the registry into OSGi services. [More details here](../registrars/README.md).
  
When a bundle is stopped : 

- The script is unregistered and removed from the list of scripts
- Version number is incremented to invalidate the existing contexts in the pool

Note : The goal of these initialization scripts is to initialize the Javascript context just after their 
creation, not to execute code on module start. The scripts will be called everytime a Javascript context needs to be created.
