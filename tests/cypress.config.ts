import { defineConfig } from 'cypress'

export default defineConfig({
    chromeWebSecurity: false,
    failOnStatusCode: false,
    defaultCommandTimeout: 30000,
    videoUploadOnPasses: false,
    reporter: 'cypress-multi-reporters',
    reporterOptions: {
        configFile: 'reporter-config.json',
    },
    env: {
        JAHIA_USERNAME: 'root',
        JAHIA_PASSWORD: 'root1234',
    },
    screenshotsFolder: './results/screenshots',
    videosFolder: './results/videos',
    _comment:
        'fileInstallTest is being ignored since there is probably a bug when installing npm modules right after start jahia',
    viewportWidth: 1366,
    viewportHeight: 768,
    e2e: {
        // We've imported your old cypress plugins here.
        // You may want to clean this up later by importing these.
        setupNodeEvents(on, config) {
            // eslint-disable-next-line @typescript-eslint/no-var-requires
            return require('./cypress/plugins/index.js')(on, config)
        },
        excludeSpecPattern: 'fileInstallTest.spec.ts',
    },
})
