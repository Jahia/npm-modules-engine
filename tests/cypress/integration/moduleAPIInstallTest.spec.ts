describe('Install NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`

    it('Install tgz bundle success path', () => {
        const fileName = 'dummy.tgz'

        // Install new Bundle
        cy.fixture(fileName, 'binary')
            .then((binary) => Cypress.Blob.binaryStringToBlob(binary))
            .then((blob) => {
                // Build up the form
                const formData = new FormData()
                formData.set('bundle', blob, fileName) //adding a file to the form
                formData.set('start', 'true') //adding a plain input to the form

                // Perform the request
                cy.apiRequest('POST', bundleApiUrl, formData, authorization, null, (response) => {
                    expect(response.status).to.eq(200)
                    expect(response.response).to.contains('successful')
                })
            })
    })
})
