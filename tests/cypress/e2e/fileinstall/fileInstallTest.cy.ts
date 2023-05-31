describe('Install NPM Modules via Jahia-Core FileInstall', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const localStateAPI = bundleApiUrl + '/org.jahia.modules/npm-module-example/1.0.0/_localState'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`
    const fullLocalStateAPI = 'http://localhost:8080' + localStateAPI

    it('Check if bundle is Active', () => {
        // Check if bundle is Active
        cy.request({
            method: 'GET',
            url: fullLocalStateAPI,
            headers: {
                Authorization: authorization,
            },
        }).then((response) => {
            expect(response.status).to.eq(200)
            expect(response.body).to.eq('ACTIVE')
        })
    })
})
