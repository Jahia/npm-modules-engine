import {addSimplePage} from '../../utils/Utils';
import {addNode} from '@jahia/cypress';

describe('Test GQL', () => {
    before('Create test page and contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testJGQL', 'testJGQL', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testJGQL/pagecontent',
                name: 'test',
                primaryNodeType: 'npmExample:testJGQL',
                properties: [
                    {name: 'jcr:title', value: 'NPM test component'},
                    {name: 'prop1', value: 'prop1 value'},
                    {name: 'prop2', value: 'prop2 value'},
                    {
                        name: 'propRichText',
                        value: '<p data-testid="propRichTextValue">Hello this is a sample rich text</p>'
                    }
                ]
            });
        });
        addSimplePage('/sites/npmTestSite/home', 'testJGQLReact', 'testJGQLReact', 'en', 'simpleReact', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testJGQLReact/pagecontent',
                name: 'testReact',
                primaryNodeType: 'npmExample:testJGQL',
                mixins: ['jmix:renderable'],
                properties: [
                    {name: 'jcr:title', value: 'NPM test component'},
                    {name: 'prop1', value: 'prop1 value'},
                    {name: 'prop2', value: 'prop2 value'},
                    {
                        name: 'propRichText',
                        value: '<p data-testid="propRichTextValue">Hello this is a sample rich text</p>'
                    },
                    {name: 'j:view', value: 'react'}
                ]
            });
        });
    });

    ['testJGQL', 'testJGQLReact'].forEach(pageName => {
        it(`${pageName}: Check GQL execution in current view`, function () {
            cy.login();
            cy.visit(`/cms/render/default/en/sites/npmTestSite/home/${pageName}.html`);
            cy.get('li[data-testid="j:nodename"]').should('contain', 'test');
            cy.get('li[data-testid="jcr:title"]').should('contain', 'NPM test component');
            cy.get('li[data-testid="prop1"]').should('contain', 'prop1 value');
            cy.get('li[data-testid="prop2"]').should('contain', 'prop2 value');
            cy.get('li[data-testid="propRichText"]').should('contain', 'Hello this is a sample rich text');
            cy.logout();
        });
    });
});
