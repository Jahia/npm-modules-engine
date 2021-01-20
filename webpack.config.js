const path = require('path');

module.exports = (env, argv) => {
    let _argv = argv || {};

    let config = {
        entry: {
            main: './src/javascript/entry.js'
        },
        output: {
            path: path.resolve(__dirname, 'src/main/resources/serverjs')
        }
    };

    return config;
};
