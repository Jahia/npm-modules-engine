const path = require('path');
const WebpackShellPluginNext = require('webpack-shell-plugin-next');
const ExtraWatchWebpackPlugin = require('extra-watch-webpack-plugin');

module.exports = env => {
    const config = {
        entry: {
            main: path.resolve(__dirname, 'src/index')
        },
        output: {
            path: path.resolve(__dirname, 'dist')
        },
        externalsPresets: {node: true},
        externals: {
            '@jahia/js-server-core': 'jsServerCoreLibraryBuilder.getLibrary()',
            react: 'jsServerCoreLibraryBuilder.getSharedLibrary(\'react\')',
            'react-i18next': 'jsServerCoreLibraryBuilder.getSharedLibrary(\'react-i18next\')',
            i18next: 'jsServerCoreLibraryBuilder.getSharedLibrary(\'i18next\')',
            'styled-jsx/style': 'jsServerCoreLibraryBuilder.getSharedLibrary(\'styled-jsx\')',
        },
        resolve: {
            extensions: ['.js', '.jsx']
        },
        module: {
            rules: [
                {
                    test: /\.jsx$/,
                    include: [path.join(__dirname, 'src')],
                    use: {
                        loader: 'babel-loader',
                        options: {
                            presets: [
                                ['@babel/preset-env', {modules: false, targets: {safari: '7', ie: '10'}}],
                                '@babel/preset-react'
                            ],
                            plugins: [
                                'styled-jsx/babel'
                            ]
                        }
                    }
                }
            ]
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
                    'package.json'
                ]
            })
        ],
        devtool: 'inline-source-map'
    };

    if (env.deploy) {
        config.plugins.push(
            new WebpackShellPluginNext({
                onAfterDone: {
                    scripts: ['yarn jahia-deploy pack']
                }
            })
        );
    }

    return config;
};
