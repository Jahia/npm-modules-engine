import { API } from '../../utils/API'

describe('Install NPM Modules via Jahia-Core FileInstall', () => {
    before('Install required module', () => {
        API.installBundle('npm-module-example-v1.0.0.tgz')
    })

    it('Check if bundle is Active', () => {
        // Check if bundle is Active
        API.checkModuleActivity('npm-module-example', '1.0.0').then((response) => {
            expect(response.status).to.eq(200)
            expect(response.body).to.eq('ACTIVE')
        })
    })

    after('Uninstall bundle', () => {
        API.uninstallBundle('npm-module-example', '1.0.0')
    })
})
