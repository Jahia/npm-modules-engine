import { handlebarsTestPage } from '../../page-object/handlebarsTest.page'
describe('Validating handlebars', () => {
    before(() => {
        handlebarsTestPage.goTo()
    })
    it('Check sub View Test Prop', function () {
        handlebarsTestPage.checksubViewTestProp()
    })
    it('Check view is working', function () {
        handlebarsTestPage.checkNpmViewIsWorkingDiv()
    })
    it('Check checkHandlebars Helpers', function () {
        handlebarsTestPage.checkHandlebarsHelpers()
    })
    it('Check handlebars GraphQL', function () {
        handlebarsTestPage.checkGraphQL()
    })
    it('Check Prop1', function () {
        handlebarsTestPage.checkProp1()
    })
    it('Check Components', function () {
        handlebarsTestPage.checkComponents()
    })
})
