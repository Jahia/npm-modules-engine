import { addNode, enableModule, publishAndWaitJobEnding } from '@jahia/cypress'
import { addSimplePage } from '../../utils/Utils'

describe('Check on bound components', () => {
    const siteKey = 'npmTestSite'

    const addEvent = (name: string, title: string, startDate: Date, endDate?: Date) => {
        addNode({
            parentPathOrId: `/sites/${siteKey}/home/events/events`,
            name: name,
            primaryNodeType: 'jnt:event',
            properties: [
                { name: 'jcr:title', value: title, language: 'en' },
                { name: 'startDate', type: 'DATE', value: startDate },
                { name: 'endDate', type: 'DATE', value: endDate },
            ],
        })
    }
    before(() => {
        enableModule('calendar', siteKey)
        enableModule('event', siteKey)
    })

    const validateNumberOfEventInCalendar = (expectedNumber: number) => {
        cy.get(`span[class*="fc-event-title"]:contains("${expectedNumber}")`).should('exist')
    }

    const addEventPageAndEvents = (thenFunction: () => void) => {
        return addSimplePage(`/sites/${siteKey}/home`, 'events', 'Events page', 'en', 'events', [
            {
                name: 'events',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            const today = new Date()
            const tomorrow = new Date()
            tomorrow.setDate(tomorrow.getDate() + 1)

            addEvent('event-a', 'The first event', today, tomorrow)
            addEvent('event-b', 'The second event', today)
            if (thenFunction) {
                thenFunction()
            }
        })
    }
    it('Verify existing .jsp component like: calendar is correctly bound to events', function () {
        cy.login()
        addEventPageAndEvents(() => {
            cy.visit(`/jahia/page-composer/default/en/sites/${siteKey}/home/events.html`)
            cy.visit(`/cms/render/default/en/sites/${siteKey}/home/events.html`)
            validateNumberOfEventInCalendar(2)
        })
        cy.logout()
    })

    it('Verify that the calendar is correctly refreshed once a new event is added', function () {
        cy.login()
        addEventPageAndEvents(() => {
            publishAndWaitJobEnding(`/sites/${siteKey}/home/events`)
            cy.visit(`/sites/${siteKey}/home/events.html`, { failOnStatusCode: false })

            const inTwoDays = new Date()
            inTwoDays.setDate(inTwoDays.getDate() + 2)
            addEvent('event-c', 'The third event', inTwoDays)

            publishAndWaitJobEnding(`/sites/${siteKey}/home/events`)

            cy.visit(`/jahia/page-composer/default/en/sites/${siteKey}/home/events.html`)
            cy.visit(`/sites/${siteKey}/home/events.html`, { failOnStatusCode: false })

            validateNumberOfEventInCalendar(2)
            validateNumberOfEventInCalendar(1)
        })
        cy.logout()
    })
})
