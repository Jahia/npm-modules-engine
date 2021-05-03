import { BasePage } from './base.page'
import 'cypress-file-upload'

class ManageModulesPage extends BasePage {
    elements = {
        manageModuleIframeSrc: "/cms/adminframe/default/en/settings.manageModules.html?redirect=false",
        moduleFileUpload: "#moduleFileUpload",
        btnUpload: "#btnUpload",
        alertText: ".alert",
        dataTablesFilter: ".dataTables_filter",
        resultTable: ".card",
    }

    goTo() {
        cy.goTo(this.elements.manageModuleIframeSrc)
        cy.get('.btn-primary').click()
        return this
    }


    uploadModuleViaApi(fileName: string) {
        const bundleApiUrl = '/modules/api/bundles'
        const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`

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


    }

    uploadModule(fileName: string) {

        // cy.fixture(fileName, 'binary')
        //     .then((binary) => {
        //         Cypress.Blob.binaryStringToBlob(binary)
        //     })
        //     .then(fileContent => {
        //         cy.get(this.elements.moduleFileUpload).attachFile({
        //             fileContent,
        //             fileName,
        //             mimeType: 'application/gzip',
        //             encoding:'binary',
        //             lastModified: new Date().getTime()
        //         })
        //     })

        cy.get(this.elements.moduleFileUpload).attachFile(fileName)
        cy.get(this.elements.btnUpload).click()


        return this
    }

    assertAlert(alertMessage) {
        cy.get(this.elements.alertText).invoke('text').then((text) => {
            expect(text.trim()).contains(alertMessage)
        })
    }

    assertModuleInResults(moduleName){
        cy.get(this.elements.dataTablesFilter).type(moduleName)
        expect(cy.get(this.elements.resultTable).contains(moduleName))
    }

}

export const manageModules = new ManageModulesPage()




