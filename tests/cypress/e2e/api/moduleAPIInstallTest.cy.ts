import { API } from '../../utils/API'

describe('Install NPM Modules via Module Manager API', () => {
    it('Install tgz bundle success path', () => {
        const fileName = 'npm-module-example-v1.0.0.tgz'
        API.installBundle(fileName).then((response) => {
            expect(response.status).to.eq(200)
            const bodyString = Cypress.Blob.arrayBufferToBinaryString(response.body)
            const body = JSON.parse(bodyString)
            expect(body.message).to.contains('successful')
        })
    })

    after('Uninstall bundle', () => {
        API.uninstallBundle('npm-module-example', '1.0.0')
    })
})
