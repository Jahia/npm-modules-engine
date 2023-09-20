import {addNode} from "@jahia/cypress";

describe('Test on render and createContentButtons helpers', () => {
    it('should display page composer create button correctly using createContentButtons helper', function () {
        cy.login()
        cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/testPage`)
        cy.iframe('[data-sel-role="page-builder-frame-active"]', {timeout: 90000, log: true}).within(() => {
            cy.get('button[data-sel-role="jnt:text"]').should('exist')
        });
        cy.logout()
    })

    it('should render jnt:text JSON node', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="component-text-json-node"]').should('contain', 'JSON node rendered')
        cy.logout()
    });

    it('should render jnt:text JSON node in config: OPTION', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="component-text-json-node-option"]').should('contain', 'JSON node rendered with option config')
        cy.logout()
    });

    it('should render npmExample:test JSON node in config: OPTION with view: sub', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="component-npm-json-node-option"]')
            .should('contain', 'prop1 value it is')
        cy.logout()
    });

    it('should render JSON node with INCLUDE config', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="component-npm-node-include"]').should('contain', 'prop1 value')
        cy.logout()
    });

    it('should render existing child node using relative path', function () {
        cy.login()
        addNode({
            parentPathOrId: `/sites/npmTestSite/home/testPage/pagecontent/test`,
            name: 'simpletext',
            primaryNodeType: 'jnt:text',
            properties: [
                { name: 'text', value: 'Child node rendered using relative path', language: 'en' }
            ],
        })
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="component-text-child-node"]').should('contain', 'Child node rendered using relative path')
        cy.logout()
    });
})
