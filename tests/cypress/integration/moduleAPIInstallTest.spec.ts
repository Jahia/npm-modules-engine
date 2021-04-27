describe('Install NPM Modules via Module Manager API', () => {
    const bundleApiUrl = '/modules/api/bundles'
    const uninstallAPI = bundleApiUrl + '/org.jahia.modules/npm-plugin-example/1.0.0/_uninstall'
    // const localStateAPI = bundleApiUrl + '/org.jahia.modules/npm-plugin-example/1.0.0/_localState'
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

        // // // Check if bundle is Active
        // cy.apiRequest('GET', localStateAPI, null, authorization, null, (response) => {
        //     expect(response.status).to.eq(200)
        //     expect(response.response).to.eq('"ACTIVE"')
        // })
    })

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

    // it('Uninstall tgz bundle success path', () => {
    //     cy.apiRequest('POST', uninstallAPI, null, authorization, 'application/x-www-form-urlencoded', (response) => {
    //         expect(response.status).to.eq(200)
    //     })
    // })

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

    it('Uninstall tgz bundle success path', () => {
        cy.apiRequest('POST', uninstallAPI, null, authorization, 'application/x-www-form-urlencoded', (response) => {
            expect(response.status).to.eq(200)
        })
    })
})
