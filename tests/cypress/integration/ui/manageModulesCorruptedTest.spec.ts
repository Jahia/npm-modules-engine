import {manageModules} from '../../page-object/manageModules.page'

describe('Install TGZ Corrupted File', () => {

    it('should fail - corrupted file', function () {
        manageModules.goTo()
        manageModules.uploadModule('dummy-corrupted.tgz')
        manageModules.assertAlert('Module upload failed: Not in GZIP format')
    })

})
