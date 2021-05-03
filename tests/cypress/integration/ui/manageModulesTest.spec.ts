import {manageModules} from '../../page-object/manageModules.page'

describe('navigation to manageModules', () => {

    it('Install tgz bundle via UI success path', function () {
        manageModules.goTo()
        //manageModules.uploadModuleViaApi('dummy.tgz')
        manageModules.uploadModule('dummy.tgz')
        // manageModules.assertAlert('Module npm-plugin-example (1.0.0) has been successfully uploaded and started. Please check its status in the list.')
        //manageModules.assertModuleInResults('npm-plugin-example')
    })

    it('should fail - not Jahia module', function () {
        manageModules.goTo()
        manageModules.uploadModule('dummy.any')
        manageModules.assertAlert('The uploaded file is not a valid Jahia module.')
    })

    it('should fail - corrupted file', function () {
        manageModules.goTo()
        manageModules.uploadModule('dummy-corrupted.tgz')
        manageModules.assertAlert('Module upload failed: Not in GZIP format')
    })

})
