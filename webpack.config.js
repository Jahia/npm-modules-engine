const path = require('path');
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin")
const {CycloneDxWebpackPlugin} = require('@cyclonedx/webpack-plugin');

/** @type {import('@cyclonedx/webpack-plugin').CycloneDxWebpackPluginOptions} */
const cycloneDxWebpackPluginOptions = {
    specVersion: '1.4',
    rootComponentType: 'library',
    outputLocation: './bom'
};

module.exports = (env, argv) => {
    let config = {
        entry: {
            main: path.resolve(__dirname, 'src/javascript/index')
        },
        output: {
            path: path.resolve(__dirname, 'src/main/resources/META-INF/js')
        },
        externals: {
            '@jahia/js-server-core-builder': 'jsServerCoreLibraryBuilder',
            '@jahia/js-server-core-private': 'jsServerCoreLibraryBuilder.getLibrary()',
        },
        resolve: {
            alias: {
                'handlebars': 'handlebars/dist/cjs/handlebars.js',
            },
            fallback: {
                "fs": false,
            },
            extensions: ['.mjs', '.js', '.jsx']
        },
        module: {
            rules: [
                {
                    test: /\.js$/,
                    include:[
                        path.resolve(__dirname, "node_modules/create-frame"),
                        path.resolve(__dirname, "node_modules/handlebars-helpers")
                    ],
                    loader: 'unlazy-loader'
                },
                {
                    test: /\.jsx$/,
                    include: [
                        path.join(__dirname, 'src'),
                        path.resolve(__dirname, "node_modules/@jahia/js-server-core")
                    ],
                    use: {
                        loader: 'babel-loader',
                        options: {
                            presets: [
                                ['@babel/preset-env', {modules: false, targets: {safari: '7', ie: '10'}}],
                                '@babel/preset-react'
                            ]
                        }
                    }
                }
            ]
        },
        plugins: [
            new NodePolyfillPlugin({
                excludeAliases: ["console"]
            }),
            new CycloneDxWebpackPlugin(cycloneDxWebpackPluginOptions)
        ],
        devtool: "inline-source-map",
        mode: "development"
    };

    return config;
};
