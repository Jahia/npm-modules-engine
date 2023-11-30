import {addNode, enableModule, publishAndWaitJobEnding} from '@jahia/cypress';
import {addSimplePage} from '../../utils/Utils';

describe('Check on bound components', () => {
    const siteKey = 'npmTestSite';

    before(() => {
        enableModule('calendar', siteKey);
        enableModule('event', siteKey);
    });

    const addEvent = event => {
        addNode({
            parentPathOrId: `/sites/${siteKey}/home/${event.pageName}/events`,
            name: event.name,
            primaryNodeType: 'jnt:event',
            properties: [
                {name: 'jcr:title', value: event.title, language: 'en'},
                {name: 'startDate', type: 'DATE', value: event.startDate},
                {name: 'endDate', type: 'DATE', value: event.endDate},
                {name: 'eventsType', value: event.eventsType ? event.eventsType : 'meeting'}
            ]
        });
    };

    const validateNumberOfEventInCalendar = (expectedNumber: number) => {
        cy.get(`span[class*="fc-event-title"]:contains("${expectedNumber}")`).should('exist');
    };

    const addEventPageAndEvents = (reactTest: boolean, pageName: string, thenFunction: () => void) => {
        return addSimplePage(
            `/sites/${siteKey}/home`,
            pageName,
            'Events page',
            'en',
            reactTest ? 'eventsReact' : 'events',
            [
                {
                    name: 'events',
                    primaryNodeType: 'jnt:contentList'
                }
            ]
        ).then(() => {
            const today = new Date();
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);

            addEvent({
                pageName,
                name: 'event-a',
                title: 'The first event',
                startDate: today,
                endDate: tomorrow
            });
            addEvent({
                pageName,
                name: 'event-b',
                title: 'The second event',
                startDate: today
            });
            if (thenFunction) {
                thenFunction();
            }
        });
    };

    [false, true].forEach(reactTest => {
        it(`${
            reactTest ? 'React' : 'Handlebar'
        }: Verify calendar (.jsp content in the template) is correctly bound to the events list`, function () {
            cy.login();
            const pageName = 'test1' + (reactTest ? 'React' : '');
            addEventPageAndEvents(reactTest, pageName, () => {
                cy.visit(`/jahia/page-composer/default/en/sites/${siteKey}/home/${pageName}.html`);
                cy.visit(`/cms/render/default/en/sites/${siteKey}/home/${pageName}.html`);
                validateNumberOfEventInCalendar(2);
            });
            cy.logout();
        });

        it(`${
            reactTest ? 'React' : 'Handlebar'
        }: Verify that the calendar is correctly refreshed once a new event is added`, function () {
            cy.login();
            const pageName = 'test2' + (reactTest ? 'React' : '');
            addEventPageAndEvents(reactTest, pageName, () => {
                publishAndWaitJobEnding(`/sites/${siteKey}/home/${pageName}`);
                cy.visit(`/sites/${siteKey}/home/${pageName}.html`, {failOnStatusCode: false});

                const inTwoDays = new Date();
                inTwoDays.setDate(inTwoDays.getDate() + 2);
                addEvent({
                    pageName,
                    name: 'event-c',
                    title: 'The third event',
                    startDate: inTwoDays
                });

                publishAndWaitJobEnding(`/sites/${siteKey}/home/${pageName}`);

                cy.visit(`/jahia/page-composer/default/en/sites/${siteKey}/home/${pageName}.html`);
                cy.visit(`/sites/${siteKey}/home/${pageName}.html`, {failOnStatusCode: false});

                validateNumberOfEventInCalendar(2);
                validateNumberOfEventInCalendar(1);
            });
            cy.logout();
        });

        it(`${reactTest ? 'React' : 'Handlebar'}: Verify that the facets is working correctly`, function () {
            cy.login();
            const pageName = 'test3' + (reactTest ? 'React' : '');
            addEventPageAndEvents(reactTest, pageName, () => {
                // Create events with event type for facets
                const today = new Date();
                const tomorrow = new Date();
                tomorrow.setDate(tomorrow.getDate() + 1);
                addEvent({
                    pageName,
                    name: 'event-meeting',
                    title: 'The meeting event',
                    startDate: today,
                    endDate: tomorrow,
                    eventsType: 'meeting'
                });
                addEvent({
                    pageName,
                    name: 'event-consumerShow',
                    title: 'The consumerShow event',
                    startDate: today,
                    endDate: tomorrow,
                    eventsType: 'consumerShow'
                });
                publishAndWaitJobEnding(`/sites/${siteKey}/home/${pageName}`);

                // Check facets display
                cy.visit(`/sites/${siteKey}/home/${pageName}.html`, {failOnStatusCode: false});
                cy.get('.eventsListItem').should('have.length', 4);
                cy.get('div[class*="facetsList"] a:contains("consumerShow")').should('exist');
                cy.get('div[class*="facetsList"] a:contains("meeting")').should('exist');

                // Activate consumerShow facet
                cy.get('div[class*="facetsList"] a:contains("consumerShow")').click();
                cy.get('.eventsListItem').should('have.length', 1);

                // Deactivate consumerShow facet
                cy.get('a:contains("remove")').click();
                cy.get('.eventsListItem').should('have.length', 4);

                // Activate meeting facet
                cy.get('div[class*="facetsList"] a:contains("meeting")').click();
                cy.get('.eventsListItem').should('have.length', 3);

                // Deactivate consumerShow facet
                cy.get('a:contains("remove")').click();
                cy.get('.eventsListItem').should('have.length', 4);
            });
            cy.logout();
        });
    });
});
