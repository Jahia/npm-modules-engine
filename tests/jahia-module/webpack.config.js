const path = require('path')
const WebpackShellPluginNext = require('webpack-shell-plugin-next')
const ExtraWatchWebpackPlugin = require('extra-watch-webpack-plugin')

module.exports = (env) => {
    const config = {
        entry: {
            main: path.resolve(__dirname, 'src/index'),
        },
        output: {
            path: path.resolve(__dirname, 'dist'),
        },
        externalsPresets: { node: true },
        externals: {
            '@jahia/server-helpers': 'jahiaHelpers',
        },
        plugins: [
            new ExtraWatchWebpackPlugin({
                files: [
                    'src/**/*',
                    'components/**/*',
                    'views/**/*',
                    'images/**/*',
                    'css/**/*',
                    'javascript/**/*',
                    'locales/**/*.json',
                    'resources/**/*.properties',
                    'definitions.cnd',
                    'import.xml',
                    'package.json',
                ],
            }),
        ],
        devtool: 'inline-source-map',
    }

    if (env.deploy) {
        config.plugins.push(
            new WebpackShellPluginNext({
                onBuildEnd: {
                    scripts: ['yarn pack && yarn deploy'],
                },
            }),
        )
    } else if (env.remoteDeploy) {
        config.plugins.push(
            new WebpackShellPluginNext({
                onBuildEnd: {
                    scripts: ['yarn pack && yarn remoteDeploy'],
                },
            }),
        )
    }

    return config
}
