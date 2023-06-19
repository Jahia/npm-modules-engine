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
            '@jahia/server-helpers': 'jahiaHelpers'
        },
        resolve: {
            alias: {
                'handlebars': 'handlebars/dist/cjs/handlebars.js',
            },
            fallback: {
                "fs": false,
            }
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
