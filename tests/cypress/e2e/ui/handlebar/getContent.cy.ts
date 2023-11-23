import {addSimplePage} from "../../../utils/Utils";
import {addNode} from "@jahia/cypress";

describe('Test on getContent helper', () => {
    before('Create NPM test site', () => {
        addSimplePage(`/sites/npmTestSite/home`, 'testJGetContent', 'testJGetContent', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testJGetContent/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testJGetContent'
            })
        })
    })
    
    it('Check getContent helper with includeChildren', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testJGetContent.html`)

        cy.get('div[data-testid="getContent_site_withChildren_contents_name"]').should('contain', 'contents')
        cy.get('div[data-testid="getContent_site_withChildren_contents_path"]').should(
            'contain',
            '/sites/npmTestSite/contents',
        )
        cy.get('div[data-testid="getContent_site_withChildren_contents_nodeType"]').should(
            'contain',
            'jnt:contentFolder',
        )

        cy.get('div[data-testid="getContent_site_withChildren_home_name"]').should('contain', 'home')
        cy.get('div[data-testid="getContent_site_withChildren_home_path"]').should('contain', '/sites/npmTestSite/home')
        cy.get('div[data-testid="getContent_site_withChildren_home_nodeType"]').should('contain', 'jnt:page')

        cy.logout()
    })
})
