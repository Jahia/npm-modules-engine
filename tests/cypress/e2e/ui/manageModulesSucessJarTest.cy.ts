import { ManageModulesPage } from '../../page-object/manageModules.page'

describe('Install JAR bundle success path', () => {
    it('Install jar bundle via UI success path', function () {
        cy.login()
        const manageModules = new ManageModulesPage()
        ManageModulesPage.visit()
        manageModules.uploadModule('sandbox.jar')
        manageModules.assertAlert('Module sandbox (1.6.0.SNAPSHOT) has been successfully uploaded and started')
        manageModules.assertModuleInResults('Sandbox Module')
        cy.logout()
    })
})
