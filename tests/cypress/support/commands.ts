// Load type definitions that come with Cypress module
/// <reference types="cypress" />

// eslint-disable-next-line @typescript-eslint/no-namespace
declare namespace Cypress {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    interface Chainable {
        /**
         * Custom command to navigate to url with default authentication
         * @example cy.goTo('/start')
         */
        goTo(value: string): Chainable<Element>
        apiRequest(
            method: string,
            url: string,
            formData: FormData,
            authorization: string,
            contentType: string,
            done,
        ): Chainable<Element>
    }
}

Cypress.Commands.add('goTo', function (url: string) {
    cy.visit(url, {
        auth: {
            username: Cypress.env('JAHIA_USERNAME'),
            password: Cypress.env('JAHIA_PASSWORD'),
        },
    })
})

Cypress.Commands.add('apiRequest', (method, url, formData, authorization, contentType, done) => {
    const xhr = new XMLHttpRequest()
    xhr.open(method, url)
    xhr.onload = function () {
        done(xhr)
    }
    xhr.onerror = function () {
        done(xhr)
    }

    if (contentType != null) {
        xhr.setRequestHeader('Content-type', contentType)
    }

    xhr.setRequestHeader('Authorization', authorization)
    xhr.send(formData)
})
