const dotenv = require('dotenv');

module.exports = (on, config) => {
    dotenv.config({path:'./run-artifacts/cypress.env'})

    config.baseUrl = process.env.JAHIA_URL;
    config.env.SUPER_USER_PASSWORD = process.env.SUPER_USER_PASSWORD;

    return config
}
