const path = require('path');
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin")

module.exports = (env, argv) => {
    let config = {
        entry: {
            main: path.resolve(__dirname, 'src/javascript/index')
        },
        output: {
            path: path.resolve(__dirname, 'src/main/resources/META-INF/js')
        },
        externals: {
            '@jahia/js-server-core-builder': 'jsServerEngineLibraryBuilder',
            '@jahia/js-server-core-private': 'jsServerEngineLibraryBuilder.getLibrary()',
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
            })
        ],
        devtool: "inline-source-map",
        mode: "development"
    };

    return config;
};
