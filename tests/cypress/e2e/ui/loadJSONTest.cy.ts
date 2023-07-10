describe('Test on loadJSON helper', () => {
    it('Check loadJSON for view relative file', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="loadJSON-viewRelative"]').should('contain', 'This is a view relative component')
        cy.logout()
    })

    it('Check loadJSON for project absolute file', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testPage.html`)
        cy.get('div[data-testid="loadJSON-absolute"]').should('contain', 'This is a global component')
        cy.logout()
    })
})
