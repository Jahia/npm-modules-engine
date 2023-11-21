import { addNode } from '@jahia/cypress'
import 'cypress-wait-until'
import { addSimplePage } from '../../utils/Utils'

describe('jArea helper test', () => {
    before('Create NPM test site', () => {
        addSimplePage(`/sites/npmTestSite/home`, 'testJArea', 'testJArea', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testJArea/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testAreas',
            })
        })

        addSimplePage(`/sites/npmTestSite/home`, 'testJAreaReact', 'testJAreaReact', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testJAreaReact/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testAreasReact',
            })
        })
    })
    ;['testJArea', 'testJAreaReact'].forEach((pageName) => {
        it(`${pageName}: Basic Area test`, () => {
            cy.login()
            cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/${pageName}`)
            cy.iframe('#page-builder-frame-1').within(() => {
                cy.get('div[data-testid="basicArea"]').find('div[type="area"]').should('be.visible')
            })
            cy.logout()
        })

        it(`${pageName}: Allowed types area`, () => {
            cy.login()
            cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/${pageName}`)
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
            cy.logout()
        })

        it(`${pageName}: Number of items area`, () => {
            cy.login()
            cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/${pageName}`)
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/${pageName}/numberOfItemsArea`,
                name: 'item1',
                primaryNodeType: 'jnt:bigText',
            })
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/${pageName}/numberOfItemsArea`,
                name: 'item2',
                primaryNodeType: 'jnt:bigText',
            })
            cy.reload()
            cy.iframe('#page-builder-frame-1').within(() => {
                cy.get('div[data-testid="numberOfItemsArea"]').find('div[type="placeholder"]').should('not.be.visible')
            })
            cy.logout()
        })

        it(`${pageName}: areaView Area`, () => {
            cy.login()
            cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/${pageName}`)
            cy.iframe('#page-builder-frame-1').within(() => {
                cy.get('div[data-testid="areaViewArea"]').find('ul[class*="dropdown"]').should('be.visible')
            })
            cy.logout()
        })

        it(`${pageName}: subNodesView Area`, () => {
            cy.login()
            cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/${pageName}`)
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/${pageName}/subNodesViewArea`,
                name: 'item1',
                primaryNodeType: 'jnt:bigText',
            })
            cy.reload()
            cy.iframe('#page-builder-frame-1').within(() => {
                cy.get('div[data-testid="subNodesViewArea"]').find('a').contains('item1')
            })
            cy.logout()
        })
    })
})
