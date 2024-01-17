<<<<<<< HEAD
import {addNode} from '@jahia/cypress';
import {addSimplePage} from '../../../utils/Utils';
=======
import { addNode } from '@jahia/cypress';
import { addSimplePage } from '../../../utils/Utils';
>>>>>>> 3c48b34 (Add tests for JRender edit default)

describe('jRender should be editable', () => {
    before('Create test contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testRenderEditable', 'Test render editable', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            },
            {
                name: 'header',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testRenderEditable/pagecontent',
                name: 'test',
<<<<<<< HEAD
                primaryNodeType: 'npmExample:testRenderEditable'
=======
                primaryNodeType: 'npmExample:testRenderEditable',
>>>>>>> 3c48b34 (Add tests for JRender edit default)
            });
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testRenderEditable/pagecontent/test',
                name: 'text',
                primaryNodeType: 'npmExample:simpleText',
                properties: [
<<<<<<< HEAD
                    {name: 'text', value: 'Testing editable', language: 'en'}
=======
                    { name: 'text', value: 'Testing editable', language: 'en' }
>>>>>>> 3c48b34 (Add tests for JRender edit default)
                ]
            });
        });
    });

    beforeEach('Login and visit', () => {
        cy.login();
        cy.visit('/jahia/jcontent/npmTestSite/en/pages/home/testRenderEditable');
    });

    it('Without parameter, text should be editable', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="npm-render-editable-default"]').find('div[class="childs"]>div>div[jahiatype="module"]').should('exist');
        });
    });

    it('With parameter set to false, text should not be editable', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="npm-render-not-editable"]').find('div[class="childs"]>div>div[jahiatype="module"]').should('not.exist');
        });
    });

    it('With parameter set to true, text should be editable', () => {
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="npm-render-editable"]').find('div[class="childs"]>div>div[jahiatype="module"]').should('exist');
        });
    });

    afterEach('Logout', () => cy.logout());
});
