// eslint-disable-next-line @typescript-eslint/no-var-requires
const codeCoverage = require('@cypress/code-coverage/task');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const installLogsPrinter = require('cypress-terminal-report/src/installLogsPrinter');
const env = require('./env');

// eslint-disable-next-line @typescript-eslint/no-var-requires
const cypressTypeScriptPreprocessor = require("./cy-ts-preprocessor");

module.exports = (on, config) => {
    env(on, config)
    codeCoverage(on, config);
    
    //https://github.com/archfz/cypress-terminal-report
    installLogsPrinter(on);
    on("file:preprocessor", cypressTypeScriptPreprocessor);
    // eslint-disable-next-line @typescript-eslint/no-var-requires
    require('@jahia/cypress/dist/plugins/registerPlugins').registerPlugins(on, config)
    return config;
};