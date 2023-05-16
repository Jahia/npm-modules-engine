import { BasePage } from './base.page'
import 'cypress-file-upload'

class HandlebarsTestPage extends BasePage {
    elements = {
        handlebarsTestPage: '/cms/editframe//default/en/sites/mySite/home.html?redirect=false',
        // handlebarsTestPage: '/cms/render/default/en/sites/mySite/home.html',
        subViewTestProp: '.headerContent > :nth-child(1)',
        npmViewIsWorkingDiv: "[data-testid*='npm-view']",
        handlebarsHelper118: "[data-testid*='helper-118']",
        handlebarsHelperPlus: "[data-testid*='helper-plus']",
        handlebarsHelperSum: "[data-testid*='helper-sum']",
        handlebarsHelperAppend: "[data-testid*='helper-append']",
        graphQLNodeName: "[data-testid*='j:nodename']",
        graphQLCreatedBy: "[data-testid*='jcr:createdBy']",
        graphQLProp1: "[data-testid*='prop1']",
        graphQLPrimaryType: "[data-testid*='jcr:primaryType']",
        graphQLOriginWS: "[data-testid*='j:originWS']",
        prop1AbsolutPath: "[data-testid*='prop1-absolute-path']",
        prop1RelativePath: "[data-testid*='prop1-relative-path']",
        prop1Resource: "[data-testid*='prop1-Resource']",
        prop1Node: "[data-testid*='prop1-node']",
        prop1UUID: "[data-testid*='prop1-uuid']",
        prop1Renderer: "[data-testid*='prop1-renderer']",
        componentBigText: "[data-testid*='component-bigText']",
        componentChildNode: "[data-testid*='component-text-child-node']",
    }

    goTo() {
        cy.goTo(this.elements.handlebarsTestPage)
        return this
    }

    checkSubViewTestProp() {
        cy.get(this.elements.subViewTestProp).should(($div) => {
            expect($div.text()).contains('prop1=123')
        })
    }

    checkNpmViewIsWorkingDiv() {
        cy.get(this.elements.npmViewIsWorkingDiv).should(($div) => {
            expect($div.text()).to.eql('NPM view working')
        })
    }

    checkHandlebarsHelpers() {
        cy.get(this.elements.handlebarsHelper118).should(($div) => {
            expect($div.text()).to.eql('i118 helper Hello !')
        })

        cy.get(this.elements.handlebarsHelperPlus).should(($div) => {
            expect($div.text()).to.eql('PLUS helper: 10')
        })

        cy.get(this.elements.handlebarsHelperSum).should(($div) => {
            expect($div.text()).to.eql('SUM helper: 0')
        })

        cy.get(this.elements.handlebarsHelperAppend).should(($div) => {
            expect($div.text()).to.eql('APPEND helper: ')
        })
    }

    checkGraphQL() {
        cy.get(this.elements.graphQLNodeName)
            .invoke('text')
            .then((text) => {
                expect(text).contains('test-2')
            })
        cy.get(this.elements.graphQLCreatedBy)
            .invoke('text')
            .then((text) => {
                expect(text).contains('root')
            })
        cy.get(this.elements.graphQLProp1)
            .invoke('text')
            .then((text) => {
                expect(text).contains('test')
            })
        cy.get(this.elements.graphQLPrimaryType)
            .invoke('text')
            .then((text) => {
                expect(text).contains('test')
            })
        cy.get(this.elements.graphQLOriginWS)
            .invoke('text')
            .then((text) => {
                expect(text).contains('default')
            })
    }

    checkProp1() {
        cy.get(this.elements.prop1AbsolutPath).should(($div) => {
            expect($div.text()).to.eql('Get property by absolute path : test')
        })

        cy.get(this.elements.prop1RelativePath).should(($div) => {
            expect($div.text()).to.eql('Relative path: test')
        })

        cy.get(this.elements.prop1Resource).should(($div) => {
            expect($div.text()).to.eql('Resource : test')
        })

        cy.get(this.elements.prop1Node).should(($div) => {
            expect($div.text()).to.eql('Node : test')
        })

        cy.get(this.elements.prop1UUID).should(($div) => {
            expect($div.text()).to.eql('uuid : test')
        })

        cy.get(this.elements.prop1Renderer).should(($div) => {
            expect($div.text()).to.eql('With renderer :  {displayName=TEST, flag=/css/blank.gif}')
        })
    }

    checkComponents() {
        cy.get(this.elements.componentBigText).should(($div) => {
            expect($div.text()).contains('Rich text here')
        })

        // Skipping this check, this should likely be re-activated as part of BACKLOG-20939
        // cy.get(this.elements.componentChildNode).should(($div) => {
        //     expect($div.text()).contains('HandlebarTest')
        // })
    }
}

export const handlebarsTestPage = new HandlebarsTestPage()
