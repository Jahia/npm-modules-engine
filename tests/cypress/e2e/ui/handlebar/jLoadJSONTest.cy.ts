import { addSimplePage } from '../../../utils/Utils'
import { addNode } from '@jahia/cypress'

describe('Test on loadJSON helper', () => {
    before('Create test page and contents', () => {
        addSimplePage(`/sites/npmTestSite/home`, 'testJLoadJSON', 'testJLoadJSON', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testJLoadJSON/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testJLoadJSON',
            })
        })
    })

    it('Check loadJSON for view relative file', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testJLoadJSON.html`)
        cy.get('div[data-testid="loadJSON-viewRelative"]').should('contain', 'This is a view relative component')
        cy.logout()
    })

    it('Check loadJSON for project absolute file', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testJLoadJSON.html`)
        cy.get('div[data-testid="loadJSON-absolute"]').should('contain', 'This is a global component')
        cy.logout()
    })
})
