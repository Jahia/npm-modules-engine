import { addSimplePage } from '../../utils/Utils'
import { addNode, deleteNode, publishAndWaitJobEnding, setNodeProperty } from '@jahia/cypress'

describe('Test on jMainContent helper', () => {
    const defaultPageInfos = {
        name: 'testJMainContent',
        path: '/sites/npmTestSite/home/testJMainContent',
        parent: '/sites/npmTestSite/home',
        nodeType: 'jnt:page',
        'property_jcr:title': 'testJMainContent title',
        'property_j:templateName': 'simple',
    }

    before('Create NPM test site', () => {
        addSimplePage(`/sites/npmTestSite/home`, 'testJMainContent', 'testJMainContent title', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testJMainContent/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testJMainContent',
            })

            publishAndWaitJobEnding('/sites/npmTestSite')
        })
    })

    after('Clean', () => {
        deleteNode('/sites/npmTestSite/home/testJMainContent')
        publishAndWaitJobEnding('/sites/npmTestSite')
    })

    it('Check jMainContent helper in preview', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testJMainContent.html`)
        testHelperResult(defaultPageInfos)
        cy.logout()
    })

    it('Check jMainContent helper in edit', function () {
        cy.login()
        cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/testJMainContent`)
        cy.iframe('[data-sel-role="page-builder-frame-active"]', { timeout: 90000, log: true }).within(() => {
            testHelperResult(defaultPageInfos)
        })
        cy.logout()
    })

    it('Check jMainContent helper in live', function () {
        cy.visit(`/sites/npmTestSite/home/testJMainContent.html`)
        testHelperResult(defaultPageInfos)
    })

    it('Check jMainContent helper after adding props to the page', function () {
        cy.login()
        setNodeProperty('/sites/npmTestSite/home/testJMainContent', 'j:tagList', ['tag1', 'tag2'], 'en')
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testJMainContent.html`)
        testHelperResult(defaultPageInfos)
        testHelperResult({
            'property_j:tagList': 'tag1,tag2',
        })
        cy.logout()
    })

    const testHelperResult = (entries) => {
        for (const key in entries) {
            cy.get(`div[data-testid="mainContent_${key}"]`).should('contain', entries[key])
        }
    }
})
