describe('Install NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    // const uninstallAPI = bundleApiUrl + '/org.jahia.modules/npm-plugin-example/1.0.0/_uninstall'
    const localStateAPI = bundleApiUrl + '/org.jahia.modules/npm-plugin-example/1.0.0/_localState'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`

    it('Check if bundle is Active', () => {
        // Check if bundle is Active
        cy.apiRequest('GET', localStateAPI, null, authorization, null, (response) => {
            expect(response.status).to.eq(200)
            expect(response.response).to.eq('"ACTIVE"')
        })
    })
})
