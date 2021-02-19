const path = require('path');

module.exports = (env, argv) => {
    let _argv = argv || {};

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
        devtool: "inline-source-map"
    };

    return config;
};
