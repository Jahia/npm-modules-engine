/*
TODO adapt
import { API } from '../../utils/API'
import { addNode, createSite, deleteSite, enableModule } from '@jahia/cypress'
import { addSimplePage } from '../../utils/Utils'

describe('Check on bound components', () => {
    const siteKey = 'siteForBoundComponentTest'

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
        const fileName = 'templates-web-blue-v1.0.0.tgz'
        API.installBundle(fileName).then((response) => {
            expect(response.status).to.eq(200)
            const bodyString = Cypress.Blob.arrayBufferToBinaryString(response.body)
            const body = JSON.parse(bodyString)
            expect(body.message).to.contains('successful')

            createSite(siteKey, {
                languages: 'en',
                templateSet: 'templates-web-blue',
                locale: 'en',
                serverName: 'localhost',
            })
            enableModule('calendar', siteKey)
            enableModule('event', siteKey)
        })
    })

    const validateCountOfEventInCalendar = () => {
        cy.get('span[class*="fc-event-title"]:contains("2")').should('exist')
    }

    it('Verify calendar is correctly bound to events', function () {
        cy.login()

        addSimplePage(`/sites/${siteKey}/home`, 'events', 'Events page', 'en', 'events', [
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
            cy.visit(`/jahia/page-composer/default/en/sites/${siteKey}/home/events.html`)

            cy.visit(`/cms/render/default/en/sites/${siteKey}/home/events.html`)
            validateCountOfEventInCalendar()
        })
        cy.logout()
    })

    after('Clean', () => {
        API.uninstallBundle('templates-web-blue', '1.0.0')
        deleteSite(siteKey)
    })
})
 */
