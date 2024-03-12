import {addNode} from '@jahia/cypress';
import {addSimplePage} from '../../../utils/Utils';

describe('Test current user', () => {
    before('Create tests contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testCurrentUser', 'Test current user', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testCurrentUser/pagecontent',
                name: 'test',
                primaryNodeType: 'npmExample:testCurrentUser'
            });
        });
    });

    it('should display the current user as root', () => {
        cy.login();
        cy.visit('/jahia/jcontent/npmTestSite/en/pages/home/testCurrentUser');
        cy.iframe('#page-builder-frame-1').within(() => {
            cy.get('div[data-testid="currentUser_name"]')
                .should('exist')
                .contains('root');
            cy.get('div[data-testid="currentUser_isRoot"]')
                .should('exist')
                .contains('true');
        });
        cy.logout();
    });
});
