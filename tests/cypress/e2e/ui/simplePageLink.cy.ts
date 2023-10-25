import { addNode, createSite, deleteSite, enableModule, publishAndWaitJobEnding } from "@jahia/cypress";

const siteKey = 'simplePageSite'
const pageName = 'simple'
const pageTitle = 'simple'
const variables = {
    parentPathOrId: `/sites/${siteKey}/home`,
    name: pageName,
    title: pageTitle,
    primaryNodeType: 'jnt:page',
    template: 'home',
    properties: [
        {name: 'jcr:title', value: pageTitle, language: 'en'},
        {name: 'j:templateName', type: 'STRING', value: 'events'}
    ],
    children: [{
        name: 'area-main',
        primaryNodeType: 'jnt:contentList',
        children: [{
            name: 'text',
            primaryNodeType: 'jnt:text',
            properties: [{language: 'en', name: 'text', type: 'STRING', value: pageName}]
        }]
    }]
};

describe('Page should create a link to preview and live', () => {
    before('createSite and page', () => {
        cy.login();
        createSite(siteKey);
        enableModule('calendar', siteKey);
        addNode(variables);
        publishAndWaitJobEnding(`/sites/${siteKey}/home`, ['en']);
        cy.logout();
    })

    it('test preview', () => {
        cy.login();
        cy.visit(`/cms/render/default/en/sites/${siteKey}/home/${pageName}.html`);
        cy.get(':contains("Page not found")').should('not.exist');
        cy.logout();
    })

    it('test live', () => {
        cy.login();
        cy.visit(`/cms/render/live/en/sites/${siteKey}/home/${pageName}.html`);
        cy.get(':contains("Page not found")').should('not.exist');
        cy.logout();
    })

    after('delete site', () => {
        deleteSite(siteKey);
    })
})