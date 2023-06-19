export class BasePage {
    /**
     * finds and element inside an iframe
     * getIframeBody will get overwritten by inheriting classes
     * @param locator
     */
    getElementInIframe(locator: string): Cypress.Chainable {
        return this.getIframeBody('').find(locator)
    }

    /**
     * waits for the body inside the iframe to appear
     * returns the body of the iframe
     * @param iframeSrc - src attribute of the iframe
     * @param timeout - Time to wait for iframe
     */
    getIframeBody(iframeSrc: string, timeout = 60000): Cypress.Chainable {
        // get the iframe > document > body
        // and retry until the body element is not empty
        return (
            cy
                .get(`iframe[src*="${iframeSrc}"]`, { timeout: timeout })
                .its('0.contentDocument.body')
                .should('not.be.empty')
                // wraps "body" DOM element to allow
                // chaining more Cypress commands, like ".find(...)"
                // https://on.cypress.io/wrap
                .then(cy.wrap)
        )
    }

    /**
     * waits for an element inside the body to appear
     * returns the element
     * useful when it's an element that appear after the iframe is loaded. like a temporary notification
     * @param iframeSrc - src attribute of the iframe
     * @param elementSelector - the element's locator
     * @param timeout - Time to wait for iframe
     */
    getIframeElement(iframeSrc: string, elementSelector: string, timeout = 60000): Cypress.Chainable {
        return cy
            .get(`iframe[src*="${iframeSrc}"]`, { timeout: timeout })
            .should(($iframe) => {
                expect($iframe.contents().find(elementSelector)).to.exist
            })
            .then(($iframe) => {
                return cy.wrap($iframe.contents().find(elementSelector))
            })
    }

    /**
     * Get any element of given type that contain given text
     * It does not require to be the direct element containing text
     * example: <span><div>mytext</div></span> getByText("span", "myText") will work
     * @param type of content to find
     * @param text to find
     */
    // eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
    getByText(type: string, text: string) {
        return cy.contains(type, text)
    }
}
