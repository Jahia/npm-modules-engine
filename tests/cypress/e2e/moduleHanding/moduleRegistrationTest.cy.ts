import { API } from '../../utils/API'
import { addNode, createSite, deleteSite, enableModule } from '@jahia/cypress'

describe('Check that components of a module are correctly registered', () => {
    const siteKey = 'registrationTestSite'

    const addSimplePage = (parentPathOrId: string, pageName: string, pageTitle: string, language: string, template) => {
        const variables = {
            parentPathOrId: parentPathOrId,
            name: pageName,
            title: pageTitle,
            primaryNodeType: 'jnt:page',
            properties: [
                { name: 'jcr:title', value: pageTitle, language: language },
                { name: 'j:templateName', type: 'STRING', value: template },
            ],
            children: [
                {
                    name: 'events',
                    primaryNodeType: 'jnt:contentList',
                },
            ],
        }
        return addNode(variables)
    }

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
        const fileName = 'engine-test-template-v1.0.0.tgz'
        API.installBundle(fileName).then((response) => {
            expect(response.status).to.eq(200)
            const bodyString = Cypress.Blob.arrayBufferToBinaryString(response.body)
            const body = JSON.parse(bodyString)
            expect(body.message).to.contains('successful')

            createSite(siteKey, {
                languages: 'en',
                templateSet: 'engine-test-template',
                locale: 'en',
                serverName: 'localhost',
            })
        })
    })

    const validateCountOfEventInCalendar = () => {
        cy.get('span[class*="fc-event-title"]:contains("2")').should('exist')
    }

    it('Verify calendar is correctly bound to events', function () {
        cy.login()

        addSimplePage(`/sites/${siteKey}/home`, 'events', 'Events page', 'en', 'events').then(() => {
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
