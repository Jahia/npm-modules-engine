import { ManageModulesPage } from '../../page-object/manageModules.page'

describe('Install TGZ bundle success path', () => {
    it('Install tgz bundle via UI success path', function () {
        cy.login()
        const manageModules = new ManageModulesPage()
        ManageModulesPage.visit()
        manageModules.uploadModule('npm-module-example-v1.0.0.tgz')
        manageModules.assertModuleInResults('npm-module-example')
        cy.logout()
    })
})
