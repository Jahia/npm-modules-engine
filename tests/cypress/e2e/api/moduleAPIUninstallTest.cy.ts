describe('Install NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const uninstallAPI = bundleApiUrl + '/org.jahia.modules/npm-module-example/1.0.0/_uninstall'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`
    const fullUninstallAPI = 'http://localhost:8080' + uninstallAPI

    it('Uninstall tgz bundle success path', () => {
        cy.request({
            method: 'POST',
            url: fullUninstallAPI,
            form: true,
            body: {
                empty: 'empty',
            },
            headers: {
                Authorization: authorization,
            },
        }).then((response) => {
            expect(response.status).to.eq(200)
        })
    })
})
