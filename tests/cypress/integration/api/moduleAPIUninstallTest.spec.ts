describe('Install NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const uninstallAPI = bundleApiUrl + '/org.jahia.modules/npm-plugin-example/1.0.0/_uninstall'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`

    it('Uninstall tgz bundle success path', () => {
        cy.apiRequest('POST', uninstallAPI, null, authorization, 'application/x-www-form-urlencoded', (response) => {
            expect(response.status).to.eq(200)
        })
    })
})
