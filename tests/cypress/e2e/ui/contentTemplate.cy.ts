import {addNode, enableModule} from '@jahia/cypress';
import {addEventPageAndEvents, addSimplePage} from '../../utils/Utils';

describe('Content templates resolution testsuite', () => {
    const siteKey = 'npmTestSite';

    const testConfigs = [
        {
            case: 'Handlebar',
            eventsPageName: 'testEvents',
            eventsPageTemplate: 'events',
            eventTemplateInURL: '',
            findDisplayableNodePageName: 'testFindDisplayableNode',
            findDisplayableNodePageTemplate: 'simple',
            findDisplayableNodeContentMixins: [],
            findDisplayableNodeContentProps: []
        },
        {
            case: 'React',
            eventsPageName: 'testEventsReact',
            eventsPageTemplate: 'eventsReact',
            eventTemplateInURL: '.fullReact',
            findDisplayableNodePageName: 'testFindDisplayableNodeReact',
            findDisplayableNodePageTemplate: 'simpleReact',
            findDisplayableNodeContentMixins: ['jmix:renderable'],
            findDisplayableNodeContentProps: [{name: 'j:view', value: 'react'}]
        }
    ];

    before('Create test page and contents', () => {
        enableModule('calendar', siteKey);
        enableModule('event', siteKey);

        for (const testConfig of testConfigs) {
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
        }
    });

    testConfigs.forEach(testConfig => {
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
    });
});
