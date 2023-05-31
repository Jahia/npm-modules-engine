import { BasePage } from './base.page'
import 'cypress-file-upload'

export class ManageModulesPage extends BasePage {
    elements = {
        moduleFileUpload: '#moduleFileUpload',
        btnUpload: '#btnUpload',
        alertText: '.alert',
        dataTablesFilter: '.dataTables_filter',
        resultTable: '.card',
    }

    static visit(): void {
        cy.visit('/cms/adminframe/default/en/settings.manageModules.html')
    }

    uploadModule(fileName: string): ManageModulesPage {
        cy.fixture(fileName, 'binary')
            .then((binary) => {
                Cypress.Blob.binaryStringToBlob(binary)
            })
            .then((fileContent) => {
                cy.get(this.elements.moduleFileUpload).attachFile({
                    fileContent,
                    fileName,
                    mimeType: 'application/gzip',
                    encoding: 'binary',
                    lastModified: new Date().getTime(),
                })
                cy.get(this.elements.btnUpload).click()
            })
        return this
    }

    assertAlert(alertMessage: string): void {
        cy.get(this.elements.alertText)
            .invoke('text')
            .then((text) => {
                expect(text.trim()).contains(alertMessage)
            })
    }

    assertModuleInResults(moduleName: string): void {
        cy.get(this.elements.dataTablesFilter).type(moduleName)
        expect(cy.get(this.elements.resultTable).contains(moduleName))
    }
}
