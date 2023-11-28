import { addNode } from '@jahia/cypress'
import { addSimplePage } from '../../utils/Utils'

describe('Test on currentContent injected data', () => {
    before('Create test page and contents', () => {
        addSimplePage(`/sites/npmTestSite/home`, 'testCurrentContent', 'testCurrentContent', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testCurrentContent/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testCurrentContent',
                properties: [
                    { name: 'jcr:title', value: 'NPM test component' },
                    { name: 'prop1', value: 'prop1 value' },
                    { name: 'propMultiple', values: ['value 1', 'value 2', 'value 3'] },
                    {
                        name: 'propRichText',
                        value: '<p data-testid="propRichTextValue">Hello this is a sample rich text</p>',
                    },
                ],
            })
        })

        addSimplePage(
            `/sites/npmTestSite/home`,
            'testCurrentContentReact',
            'testCurrentContentReact',
            'en',
            'simpleReact',
            [
                {
                    name: 'pagecontent',
                    primaryNodeType: 'jnt:contentList',
                },
            ],
        ).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testCurrentContentReact/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testCurrentContent',
                mixins: ['jmix:renderable'],
                properties: [
                    { name: 'jcr:title', value: 'NPM test component' },
                    { name: 'prop1', value: 'prop1 value' },
                    { name: 'propMultiple', values: ['value 1', 'value 2', 'value 3'] },
                    {
                        name: 'propRichText',
                        value: '<p data-testid="propRichTextValue">Hello this is a sample rich text</p>',
                    },
                    { name: 'j:view', value: 'react' },
                ],
            })
        })
    })
    ;['testCurrentContent', 'testCurrentContentReact'].forEach((pageName) => {
        it(`${pageName}: Check currentContent injected JSON node in current view`, function () {
            cy.login()
            cy.visit(`/cms/render/default/en/sites/npmTestSite/home/${pageName}.html`)
            cy.get('div[data-testid="currentContent_properties_prop1"]').should('contain', 'prop1 value')
            cy.get('div[data-testid="currentContent_properties_jcr:title"]').should('contain', 'NPM test component')
            cy.get('div[data-testid="currentContent_properties_propMultiple"]').should(
                'contain',
                'value 1,value 2,value 3',
            )
            cy.get('p[data-testid="propRichTextValue"]').should('contain', 'Hello this is a sample rich text')
            cy.get('div[data-testid="currentContent_name"]').should('contain', 'test')
            cy.get('div[data-testid="currentContent_path"]').should(
                'contain',
                `/sites/npmTestSite/home/${pageName}/pagecontent/test`,
            )
            cy.get('div[data-testid="currentContent_parent"]').should(
                'contain',
                `/sites/npmTestSite/home/${pageName}/pagecontent`,
            )
            cy.get('div[data-testid="currentContent_nodeType"]').should('contain', 'npmExample:test')
            cy.logout()
        })
    })
})
