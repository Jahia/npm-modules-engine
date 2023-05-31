import { ManageModulesPage } from '../../page-object/manageModules.page'

describe('Install TGZ Corrupted File', () => {
    it('should fail - corrupted file', function () {
        cy.login()
        const manageModules = new ManageModulesPage()
        ManageModulesPage.visit()
        manageModules.uploadModule('dummy-corrupted.tgz')
        manageModules.assertAlert('Module upload failed: Not in GZIP format')
        cy.logout()
    })
})
