import {addNode, enableModule} from '@jahia/cypress';
import {addEventPageAndEvents, addSimplePage} from '../../utils/Utils';

describe('Content templates resolution testsuite', () => {
    const siteKey = 'npmTestSite';

    const testConfig = {
        case: 'React',
        eventsPageName: 'testEventsReact',
        eventsPageTemplate: 'eventsReact',
        eventTemplateInURL: '.fullReact',
        findDisplayableNodePageName: 'testFindDisplayableNodeReact',
        findDisplayableNodePageTemplate: 'events',
        findDisplayableNodeContentMixins: ['jmix:renderable'],
        findDisplayableNodeContentProps: [{name: 'j:view', value: 'react'}]
    };

    const secondaryTestConfig = {
        case: 'React',
        nodeType: 'npmExample:testReactContentTemplate',
        template: 'default'
    };

    before('Create test page and contents', () => {
        enableModule('calendar', siteKey);
        enableModule('event', siteKey);

        addEventPageAndEvents(siteKey, testConfig.eventsPageTemplate, testConfig.eventsPageName, () => {
            addSimplePage('/sites/npmTestSite/home', testConfig.findDisplayableNodePageName, 'Simple page', 'en', testConfig.findDisplayableNodePageTemplate, [
                {
                    name: 'pagecontent',
                    primaryNodeType: 'jnt:contentList'
                }
            ]).then(() => {
                addNode({
                    parentPathOrId: `/sites/npmTestSite/home/${testConfig.findDisplayableNodePageName}/pagecontent`,
                    name: 'findDisplayableContent',
                    primaryNodeType: 'npmExample:testJFindDisplayableContent',
                    mixins: testConfig.findDisplayableNodeContentMixins,
                    properties: [
                        ...testConfig.findDisplayableNodeContentProps,
                        {name: 'target', value: `/sites/npmTestSite/home/${testConfig.eventsPageName}/events/event-a`, type: 'WEAKREFERENCE'}
                    ]
                });
            });
        });

        const pageName = `test${testConfig.case}ContentTemplate`;
        const pageNameWithView = `test${testConfig.case}ContentTemplateWithView`;
        addSimplePage('/sites/npmTestSite/home', pageName, pageName, 'en', testConfig.template, [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/${pageName}/pagecontent`,
                name: 'content',
                primaryNodeType: testConfig.nodeType
            });
        });

        addSimplePage('/sites/npmTestSite/home', pageNameWithView, pageNameWithView, 'en', testConfig.template, [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/${pageNameWithView}/pagecontent`,
                name: 'content',
                primaryNodeType: testConfig.nodeType,
                mixins: ['jmix:renderable'],
                properties: [
                    {name: 'j:view', value: 'other'}
                ]
            });
        });
    });

    it(`${testConfig.case}: Verify content template for jnt:event is correctly displayed`, function () {
        cy.login();
        cy.visit(`/jahia/page-composer/default/en/sites/${siteKey}/home/${testConfig.eventsPageName}/events/event-a${testConfig.eventTemplateInURL}.html`);
        cy.visit(`/cms/render/default/en/sites/${siteKey}/home/${testConfig.eventsPageName}/events/event-a${testConfig.eventTemplateInURL}.html`);

        // Check template is good:
        cy.get('div[class="header"]').should('be.visible');
        cy.get('div[class="nav"]').should('be.visible');
        cy.get('div[class="main"]').should('be.visible');
        cy.get('div[class="footer"]').should('be.visible');

        // Check main resource display is correct:
        cy.get('div[class="eventsBody"]').should('be.visible');
        cy.get('div[class="eventsBody"]').contains('The first event');
        cy.logout();
    });

    it(`${testConfig.case}: Verify findDisplayableNode is correctly resolving jnt:event that is using a JS content template`, function () {
        cy.login();
        cy.visit(`/jahia/page-composer/default/en/sites/${siteKey}/home/${testConfig.findDisplayableNodePageName}.html`);
        cy.visit(`/cms/render/default/en/sites/${siteKey}/home/${testConfig.findDisplayableNodePageName}.html`);

        cy.get('p[data-testid="displayableContent"]').contains(`Found displayable content: /sites/npmTestSite/home/${testConfig.eventsPageName}/events/event-a`);
        cy.logout();
    });

    it(`${secondaryTestConfig.case}: test default content template is working properly when content doesn't have specific view`, function () {
        const pageName = `test${secondaryTestConfig.case}ContentTemplate`;
        cy.login();
        cy.visit(`/cms/render/default/en/sites/${siteKey}/home/${pageName}/pagecontent/content.html`);
        // Check template is correctly resolved:
        cy.get('.header').should('exist');
        // Check content is correctly displayed:
        cy.contains('Just a normal view').should('be.visible');
        cy.logout();
    });

    it(`${secondaryTestConfig.case}: test default content template is working properly when content have specific view`, function () {
        const pageNameWithView = `test${secondaryTestConfig.case}ContentTemplateWithView`;
        cy.login();
        cy.visit(`/cms/render/default/en/sites/${siteKey}/home/${pageNameWithView}/pagecontent/content.html`);
        // Check template is correctly resolved:
        cy.get('.header').should('exist');
        // Check content is correctly displayed:
        cy.contains('Just an other normal view').should('be.visible');
        cy.logout();
    });
});
