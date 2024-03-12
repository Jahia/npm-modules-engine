import {addNode} from '@jahia/cypress';
import {addSimplePage} from '../../../utils/Utils';
describe('test isNodeType', () => {
    before('Create test contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testIsNodeType', 'Test isNodeType', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testIsNodeType/pagecontent',
                name: 'test',
                primaryNodeType: 'npmExample:testIsNodeType'
            });
        });
    });

    it('should display the node type', () => {
        cy.login();
        cy.visit('/jahia/jcontent/npmTestSite/en/pages/home/testIsNodeType');
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="currentNode_isNodeType"]')
                .should('exist')
                .contains('true');
            cy.get('div[data-testid="currentNode_isNotNodeType"]')
                .should('exist')
                .contains('false');
        });
        cy.logout();
    });
});
