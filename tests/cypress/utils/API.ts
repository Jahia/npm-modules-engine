export class API {
    static bundleApiUrl = '/modules/api/bundles'
    static authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`

    static installBundle(fileName: string): Cypress.Chainable {
        // Install new Bundle
        return cy
            .fixture(fileName, 'binary')
            .then((binary) => Cypress.Blob.binaryStringToBlob(binary))
            .then((blob) => {
                // Build up the form
                const formData = new FormData()
                formData.set('bundle', blob, fileName) //adding a file to the form
                formData.set('start', 'true') //adding a plain input to the form

                // Perform the request
                cy.request({
                    method: 'POST',
                    url: API.bundleApiUrl,
                    body: formData,
                    headers: {
                        Authorization: API.authorization,
                    },
                })
            })
    }

    static uninstallBundle(moduleName: string, moduleVersion: string): Cypress.Chainable {
        const uninstallAPI = API.bundleApiUrl + `/org.jahia.modules/${moduleName}/${moduleVersion}/_uninstall`
        return cy.request({
            method: 'POST',
            url: uninstallAPI,
            form: true,
            body: {
                empty: 'empty',
            },
            headers: {
                Authorization: API.authorization,
            },
        })
    }

    static checkModuleActivity(moduleName: string, moduleVersion: string): Cypress.Chainable {
        const localStateAPI = API.bundleApiUrl + `/org.jahia.modules/${moduleName}/${moduleVersion}/_localState`
        return cy.request({
            method: 'GET',
            url: localStateAPI,
            headers: {
                Authorization: API.authorization,
            },
        })
    }
}
