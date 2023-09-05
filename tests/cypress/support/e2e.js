// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:

import './commands'
import '@cypress/code-coverage/support'
import 'cypress-wait-until'
import { addNode, createSite, deleteSite } from '@jahia/cypress'
import { addSimplePage } from '../utils/Utils'

// eslint-disable-next-line @typescript-eslint/no-var-requires
require('@jahia/cypress/dist/support/registerSupport').registerSupport()

before('Create NPM test site', () => {
    createSite('npmTestSite', {
        languages: 'en',
        templateSet: 'npm-module-example',
        locale: 'en',
        serverName: 'localhost',
    })

    addSimplePage(`/sites/npmTestSite/home`, 'testPage', 'testPage', 'en', 'simple', [
        {
            name: 'pagecontent',
            primaryNodeType: 'jnt:contentList',
        },
    ]).then(() => {
        addNode({
            parentPathOrId: `/sites/npmTestSite/home/testPage/pagecontent`,
            name: 'test',
            primaryNodeType: 'npmExample:test',
            properties: [
                { name: 'jcr:title', value: 'NPM test component' },
                { name: 'prop1', value: 'prop1 value' },
                { name: 'propMultiple', values: ['value 1', 'value 2', 'value 3'] }
            ],
        })
    })
})

after('Clean', () => {
    deleteSite('npmTestSite')
})
