describe('Test on getContent helper', () => {
    it('Check currentContent injected JSON node in current view', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="currentContent_properties_prop1"]').should('contain', 'prop1 value')
        cy.get('div[data-testid="currentContent_properties_jcr:title"]').should('contain', 'NPM test component')
        cy.get('div[data-testid="currentContent_properties_propMultiple"]').should('contain', 'value 1,value 2,value 3')
        cy.get('p[data-testid="propRichTextValue"]').should('contain', 'Hello this is a sample rich text')
        cy.get('div[data-testid="currentContent_name"]').should('contain', 'test')
        cy.get('div[data-testid="currentContent_path"]').should(
            'contain',
            '/sites/npmTestSite/home/testPage/pagecontent/test',
        )
        cy.get('div[data-testid="currentContent_parent"]').should(
            'contain',
            '/sites/npmTestSite/home/testPage/pagecontent',
        )
        cy.get('div[data-testid="currentContent_nodeType"]').should('contain', 'npmExample:test')
        cy.logout()
    })

    it('Check getContent helper with includeChildren', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)

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
