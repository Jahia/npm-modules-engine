# NPM-MODULE-ENGINE 

This project handle the conversion of a npm moduble into an OSGi bundle and more.

# About webpack : 

Jahia’s npm module project have a very specific webpack configuration that builds the module and that pack/deploy it if needed. The pack/deploy phase is executed thanks to jahia’s custom script from the javascripts-components projects (https://github.com/Jahia/javascript-components).

## Why having the pack/deploy inside of webpack ?

The deploy phase could be outside of the webpack file, and instead be launched from the scripts of the package.json.

For example we could have this script : 

"watch": "webpack --mode=development --watch && jahia-pack && jahia-deploy",

But we can’t, because in that case, during a ‘yarn watch’, the bundle will be builded, but won’t be deployed.
Because the –watch mode will run the webpack configuration objects each time a change is made. So we need the pack/deploy scripts inside a webpack configuration

Note : The –watch mode won’t re-run the whole webpack.config.js file, so if you add a client component during a watch, the component won’t be included in the bundle. Because we’re scanning the clients components only once at the beginning of the file (not in a webpack configuration).

In case of watch mode, we want to deploy the bundle only if all the configurations are builded without errors. In the Luxe project, we have 3 configurations for example : client, css and server.

So we need to check that the 3 configurations have been builded well, and then we can run the jahia-pack and jahia-deploy scripts.

Note : In watch mode, the configurations are rebuilded only if they have changes.

## The WebpackShellPluginNext plugin :

The WebpackShellPluginNext plugin allow to trigger thoses events (and more) during or after the compilation : 

onAfterDone : Always executed at the end of the watch or build (even with compilation errors)

onDoneWatch : Always executed at the end of the watch processing (even with compilation errors)

onBeforeBuild : Executed before a build, not executed at every watch run.

OnWatchRun : Executed once just after running a 'yarn watch' command

OnFailedBuild : Executed if the configuration of the plugin have a failed build

There is no event triggered at the beginning of every watch run.

The plugin has only access to the context of the configuration in which it is declared. So the OnFailedBuild event of the plugin will be triggered only if the configuration of the script has an error.

So if you push it into a configuration without build tasks, no onBuildError events will ever be triggered, even if the other 3 configurations (client, css, server) are in error.
