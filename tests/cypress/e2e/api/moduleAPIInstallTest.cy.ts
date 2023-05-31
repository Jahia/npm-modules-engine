describe('Install NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`
    const fullBundleApiUrl = 'http://localhost:8080' + bundleApiUrl

    it('Install tgz bundle success path', () => {
        const fileName = 'npm-module-example-v1.0.0.tgz'

        // Install new Bundle
        cy.fixture(fileName, 'binary')
            .then((binary) => Cypress.Blob.binaryStringToBlob(binary))
            .then((blob) => {
                // Build up the form
                const formData = new FormData()
                formData.set('bundle', blob, fileName) //adding a file to the form
                formData.set('start', 'true') //adding a plain input to the form

                // Perform the request
                cy.request({
                    method: 'POST',
                    url: fullBundleApiUrl,
                    body: formData,
                    headers: {
                        Authorization: authorization,
                    },
                }).then((response) => {
                    expect(response.status).to.eq(200)
                    const bodyString = Cypress.Blob.arrayBufferToBinaryString(response.body)
                    const body = JSON.parse(bodyString)
                    expect(body.message).to.contains('successful')
                })
            })
    })
})
