import {manageModules} from '../../page-object/manageModules.page'

describe('Install TGZ bundle success path', () => {

    it('Install tgz bundle via UI success path', function () {
        manageModules.goTo()
        //manageModules.uploadModuleViaApi('dummy.tgz')
        manageModules.uploadModule('dummy.tgz')
        // manageModules.assertAlert('Module npm-plugin-example (1.0.0) has been successfully uploaded and started. Please check its status in the list.')
        // manageModules.assertModuleInResults('npm-plugin-example')
    })

})
