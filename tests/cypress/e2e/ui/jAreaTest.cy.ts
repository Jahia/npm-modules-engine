import { addNode, createSite, deleteSite, enableModule } from '@jahia/cypress'
import 'cypress-wait-until'

const siteKey = 'jAreaHelperTestSite'

describe('jArea helper test', () => {
    before('Create site and content', () => {
        cy.login()
        createSite(siteKey, { templateSet: 'npm-module-example', serverName: 'localhost', locale: 'en' })
        enableModule('event', siteKey)
        cy.visit(`/jahia/jcontent/${siteKey}/en/pages/home`)
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[class="article"]')
                .find('div[type="placeholder"]')
                .should('be.visible')
                .then(() => {
                    addNode({
                        parentPathOrId: `/sites/${siteKey}/home/pagecontent`,
                        name: 'testAreas',
                        primaryNodeType: 'npmExample:testAreas',
                    })
                })
        })
        cy.logout()
    })

    beforeEach('Login and visit', () => {
        cy.login()
        cy.visit(`/jahia/jcontent/${siteKey}/en/pages/home`)
    })

    it('Basic Area test', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="basicArea"]').find('div[type="area"]').should('be.visible')
        })
    })

    it('Allowed types area', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="allowedTypesArea"]')
                .find('div[type="placeholder"]')
                .then((buttons) => {
                    const selector = `div[data-jahia-id="${buttons.attr('id')}"]`
                    cy.get(selector).find('button[data-sel-role="jnt:bigText"]').should('be.visible')
                    cy.get(selector).find('button[data-sel-role="jnt:event"]').should('be.visible')
                    cy.get(selector)
                        .find('button[data-sel-role!="jnt:event"][data-sel-role!="jnt:bigText"]')
                        .should('not.exist')
                })
        })
    })

    it('Number of items area', () => {
        addNode({
            parentPathOrId: `/sites/${siteKey}/home/numberOfItemsArea`,
            name: 'item1',
            primaryNodeType: 'jnt:bigText',
        })
        addNode({
            parentPathOrId: `/sites/${siteKey}/home/numberOfItemsArea`,
            name: 'item2',
            primaryNodeType: 'jnt:bigText',
        })
        cy.reload()
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="numberOfItemsArea"]').find('div[type="placeholder"]').should('not.be.visible')
        })
    })

    it('areaView Area', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="areaViewArea"]').find('ul[class*="dropdown"]').should('be.visible')
        })
    })

    it('subNodesView Area', () => {
        addNode({
            parentPathOrId: `/sites/${siteKey}/home/subNodesViewArea`,
            name: 'item1',
            primaryNodeType: 'jnt:bigText',
        })
        cy.reload()
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="subNodesViewArea"]').find('a').contains('item1')
        })
    })

    afterEach('Logout', () => {
        cy.logout()
        cy.visit('/')
    })

    after('Delete site', () => {
        cy.login()
        deleteSite(siteKey)
        cy.logout()
    })
})
