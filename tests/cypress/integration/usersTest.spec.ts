describe('navigation to user', () => {
    it('should login', () => {
        cy.visit('/jahia/dashboard', { failOnStatusCode: false })
        cy.get('input[name=username]').type('root')
        cy.get('input[name=password]').type(Cypress.env('SUPER_USER_PASSWORD'))
        cy.get('button[type=submit]').click()
        cy.url().should('include', '/jahia/dashboard')
    })
})
