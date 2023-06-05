import { API } from '../../utils/API'

describe('Install NPM Modules via Module Manager API', () => {
    before('Install required bundle', () => {
        API.installBundle('npm-module-example-v1.0.0.tgz')
    })

    it('Uninstall tgz bundle success path', () => {
        API.uninstallBundle('npm-module-example', '1.0.0').then((response) => {
            expect(response.status).to.eq(200)
        })
    })
})
