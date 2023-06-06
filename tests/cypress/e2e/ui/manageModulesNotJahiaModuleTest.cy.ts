import { ManageModulesPage } from '../../page-object/manageModules.page'

describe('Install TGZ Not Jahia Module', () => {
    it('should fail - not Jahia module', function () {
        cy.login()
        const manageModules = new ManageModulesPage()
        ManageModulesPage.visit()
        manageModules.uploadModule('dummy.any')
        manageModules.assertAlert('The uploaded file is not a valid Jahia module.')
        cy.logout()
    })
})
