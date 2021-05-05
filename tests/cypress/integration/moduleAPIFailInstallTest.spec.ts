describe('Install wrong formats NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`

    it('Should be Unauthorized', () => {
        // Perform the request
        cy.apiRequest('POST', bundleApiUrl, null, null, null, (response) => {
            expect(response.status).to.eq(401)
            expect(response.response).to.contains('User guest is not allowed')
        })
    })

    it('Should fail requesting at least one bundle', () => {
        // Build up the form
        const formData = new FormData()
        formData.set('start', 'true') //adding a plain input to the form

        cy.apiRequest('POST', bundleApiUrl, formData, authorization, null, (response) => {
            expect(response.status).to.eq(400) //Bad Request
            expect(response.response).to.contains('At least one bundle file is required')
        })
    })

    it('Should fail installing corrupted bundle', () => {
        const fileName = 'dummy-corrupted.tgz'

        cy.fixture(fileName, 'binary')
            .then((binary) => Cypress.Blob.binaryStringToBlob(binary))
            .then((blob) => {
                // Build up the form
                const formData = new FormData()
                formData.set('bundle', blob, fileName) //adding a file to the form
                formData.set('start', 'true') //adding a plain input to the form

                cy.apiRequest('POST', bundleApiUrl, formData, authorization, null, (response) => {
                    expect(response.status).to.eq(500)
                    expect(response.response).to.contains('Error while installing bundle')
                    expect(response.response).to.contains('java.util.zip.ZipException: Not in GZIP format')
                })
            })
    })
})