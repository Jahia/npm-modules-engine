describe('Install wrong formats NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`

    it('Should be Unauthorized', () => {
        // Perform the request
        cy.request({
            method: 'POST',
            url: bundleApiUrl,
            failOnStatusCode: false,
        }).then((response) => {
            expect(response.status).to.eq(401)
            expect(response.body).to.contains('User guest is not allowed')
        })
    })

    it('Should fail requesting at least one bundle', () => {
        // Build up the form
        const formData = new FormData()
        formData.set('start', 'true') //adding a plain input to the form

        cy.request({
            method: 'POST',
            url: bundleApiUrl,
            body: formData,
            headers: {
                Authorization: authorization,
            },
            failOnStatusCode: false,
        }).then((response) => {
            expect(response.status).to.eq(400) //Bad Request
            const bodyString = Cypress.Blob.arrayBufferToBinaryString(response.body)
            const body = JSON.parse(bodyString)
            expect(body.message).to.contains('At least one bundle file is required')
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
                cy.request({
                    method: 'POST',
                    url: bundleApiUrl,
                    body: formData,
                    headers: {
                        Authorization: authorization,
                    },
                    failOnStatusCode: false,
                }).then((response) => {
                    expect(response.status).to.eq(500)
                    const bodyString = Cypress.Blob.arrayBufferToBinaryString(response.body)
                    const body = JSON.parse(bodyString)

                    expect(body.message).to.contains('Error while installing bundle')
                    expect(body.cause).to.contains('java.util.zip.ZipException: Not in GZIP format')
                })
            })
    })
})
