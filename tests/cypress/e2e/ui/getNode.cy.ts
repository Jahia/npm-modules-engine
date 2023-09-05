describe('Test on getNode helper', () => {
    it('Check currentNode injected JSON node in current view', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="currentNode_properties_prop1"]').should('contain', 'prop1 value')
        cy.get('div[data-testid="currentNode_properties_jcr:title"]').should('contain', 'NPM test component')
        cy.get('div[data-testid="currentNode_properties_propMultiple"]').should(
            'contain',
            '[value 1, value 2, value 3]',
        )
        cy.get('div[data-testid="currentNode_name"]').should('contain', 'test')
        cy.get('div[data-testid="currentNode_path"]').should(
            'contain',
            '/sites/npmTestSite/home/testPage/pagecontent/test',
        )
        cy.get('div[data-testid="currentNode_parent"]').should(
            'contain',
            '/sites/npmTestSite/home/testPage/pagecontent',
        )
        cy.get('div[data-testid="currentNode_nodeType"]').should('contain', 'npmExample:test')
        cy.logout()
    })

    it('Check getNode helper with includeChildren', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)

        cy.get('div[data-testid="getNode_site_withChildren_contents_name"]').should('contain', 'contents')
        cy.get('div[data-testid="getNode_site_withChildren_contents_path"]').should(
            'contain',
            '/sites/npmTestSite/contents',
        )
        cy.get('div[data-testid="getNode_site_withChildren_contents_nodeType"]').should('contain', 'jnt:contentFolder')

        cy.get('div[data-testid="getNode_site_withChildren_home_name"]').should('contain', 'home')
        cy.get('div[data-testid="getNode_site_withChildren_home_path"]').should('contain', '/sites/npmTestSite/home')
        cy.get('div[data-testid="getNode_site_withChildren_home_nodeType"]').should('contain', 'jnt:page')

        cy.logout()
    })
})
