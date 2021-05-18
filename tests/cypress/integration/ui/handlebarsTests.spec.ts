import { handlebarsTestPage } from '../../page-object/handlebarsTest.page'
import {manageModules} from "../../page-object/manageModules.page";
describe('Validating handlebars', () => {

   before(() => {
       manageModules.goTo()
       manageModules.uploadModule('npm-plugin-example.tgz')
   })

    // after(() => {
    //     const bundleApiUrl = '/modules/api/bundles'
    //     const uninstallAPI = bundleApiUrl + '/org.jahia.modules/npm-plugin-example/1.0.0/_uninstall'
    //     const authorization = `Basic ${btoa(Cypress.env('JAHIA_USERNAME') + ':' + Cypress.env('JAHIA_PASSWORD'))}`
    //     cy.apiRequest('POST', uninstallAPI, null, authorization, 'application/x-www-form-urlencoded', (response) => {
    //         expect(response.status).to.eq(200)
    //     })
    // })

    it('Check handlebars is working', function () {
        handlebarsTestPage.goTo()
        // cy.get('.headerContent > :nth-child(4)').contains('Rich text here')
    })
})
