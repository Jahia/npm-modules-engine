import { manageModules } from '../../page-object/manageModules.page'

describe('Install TGZ bundle success path', () => {
    it('Install jar bundle via UI success path', function () {
        manageModules.goTo()
        manageModules.uploadModule('sandbox.jar')
        manageModules.assertAlert('Module sandbox (1.6.0.SNAPSHOT) has been successfully uploaded and started')
        manageModules.assertModuleInResults('Sandbox Module')
    })
})
