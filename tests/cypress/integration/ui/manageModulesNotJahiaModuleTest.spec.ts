import {manageModules} from '../../page-object/manageModules.page'

describe('Install TGZ Not Jahia Module', () => {

    it('should fail - not Jahia module', function () {
        manageModules.goTo()
        manageModules.uploadModule('dummy.any')
        manageModules.assertAlert('The uploaded file is not a valid Jahia module.')
    })

})
