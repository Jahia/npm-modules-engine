import { createSite, deleteSite, publishAndWaitJobEnding, setNodeProperty } from '@jahia/cypress'
import 'cypress-iframe'

const siteKey = 'templateTestSite'

const checkSectionsPresence = () => {
    cy.get('div[class="header"]').should('be.visible')
    cy.get('div[class="nav"]').should('be.visible')
    cy.get('div[class="main"]').should('be.visible')
    cy.get('div[class="footer"]').should('be.visible')
}

describe('Template testsuite', () => {
    before('Create site with template', () => {
        createSite(siteKey, { templateSet: 'npm-module-example', serverName: 'localhost', locale: 'en' })
    })

    beforeEach('login & visit', () => {
        cy.login()
        cy.visit(`/jahia/jcontent/${siteKey}/en/pages/home`)
    })

    afterEach('logout', () => {
        cy.logout()
    })

    after('Delete site', () => {
        deleteSite(siteKey)
    })

    it('Verify 4 sections presence', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            checkSectionsPresence()
        })
    })

    it('Check grouping components', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('button[data-sel-role="createContent"]:first').click()
        })
        cy.get('li[role="treeitem"]:contains("npmExampleComponent")').click()
        cy.get('li[role="treeitem"]:contains("Navigation Menu")').should('be.visible')
        cy.get('li[role="treeitem"]:contains("test")').should('be.visible')
    })

    it('Check 4 sections presence in LIVE workspace', () => {
        setNodeProperty(`/sites/${siteKey}/home`, 'jcr:title', 'Home', 'en')
        setNodeProperty(`/sites/${siteKey}/home/page1`, 'jcr:title', 'Page 1', 'en')
        setNodeProperty(`/sites/${siteKey}/home/page2`, 'jcr:title', 'Page 2', 'en')
        setNodeProperty(`/sites/${siteKey}/home/page3`, 'jcr:title', 'Page 3', 'en')
        publishAndWaitJobEnding('/sites/' + siteKey)
        cy.visit('/sites/mySite/home.html')
        checkSectionsPresence()
    })
})
