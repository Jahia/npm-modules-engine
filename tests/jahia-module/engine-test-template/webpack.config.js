const path = require('path');
const WebpackShellPlugin = require('./WebpackShellPlugin');
const ExtraWatchWebpackPlugin = require('extra-watch-webpack-plugin');

module.exports = (env, argv) => {
    let config = {
        entry: {
            main: path.resolve(__dirname, 'src/index')
        },
        output: {
            path: path.resolve(__dirname, 'dist')
        },
        externals: {
            '@jahia/server-helpers': 'jahiaHelpers'
        },
        plugins: [
            new ExtraWatchWebpackPlugin({
                files: [
                    'src/**/*',
                    'components/**/*',
                    'images/**/*',
                    'css/**/*',
                    'javascript/**/*',
                    'locales/**/*.json',
                    'resources/**/*.properties',
                    'definitions.cnd',
                    'import.xml',
                    'package.json',
                    'locales/*',
                    'resources/*'
                ]
            }),
        ],
        devtool: "inline-source-map"
    };

    if (env.deploy) {
        config.plugins.push(
            new WebpackShellPlugin({
                onBuildEnd: ['yarn pack && yarn deploy']
            })
        )
    }
    if (env.remoteDeploy) {
        config.plugins.push(
            new WebpackShellPlugin({
                onBuildEnd: ['yarn pack && yarn remoteDeploy']
            })
        )
    }
    return config;
};
