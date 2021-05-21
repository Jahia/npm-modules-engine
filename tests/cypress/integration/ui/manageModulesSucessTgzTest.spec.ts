import { manageModules } from '../../page-object/manageModules.page'

describe('Install TGZ bundle success path', () => {
    it('Install tgz bundle via UI success path', function () {
        manageModules.goTo()
        manageModules.uploadModule('npm-plugin-example-v1.0.0.tgz')
        manageModules.assertModuleInResults('npm-plugin-example')
    })
})
