const dotenv = require('dotenv');

module.exports = (on, config) => {
    dotenv.config()

    config.baseUrl = process.env.JAHIA_URL;
    config.env.SUPER_USER_PASSWORD = process.env.SUPER_USER_PASSWORD;

    return config
}
