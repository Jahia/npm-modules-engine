import {addNode} from '@jahia/cypress';
import {addSimplePage} from '../../../utils/Utils';

describe('getChildNodes function test', () => {
    before('Create test page and contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testGetChildNodes', 'Test getChildNodes', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testGetChildNodes/pagecontent',
                name: 'getChildNodesTest',
                primaryNodeType: 'npmExample:testGetChildNodes',
                children: [
                    {
                        name: 'child1',
                        primaryNodeType: 'npmExample:testGetChildNodes'
                    },
                    {
                        name: 'child2',
                        primaryNodeType: 'npmExample:testGetChildNodes'
                    },
                    {
                        name: 'filtered',
                        primaryNodeType: 'npmExample:testGetChildNodes'
                    }
                ]
            });
        });
    });

    it('Verify all children returned', function () {
        cy.login();
        cy.visit('/jahia/page-composer/default/en/sites/npmTestSite/home/testGetChildNodes.html');
        cy.visit('/cms/render/default/en/sites/npmTestSite/home/testGetChildNodes.html');

        cy.get('div[data-testid="getChildNodes_all_1"]').contains('/sites/npmTestSite/home/testGetChildNodes/pagecontent/getChildNodesTest/child1');
        cy.get('div[data-testid="getChildNodes_all_2"]').contains('/sites/npmTestSite/home/testGetChildNodes/pagecontent/getChildNodesTest/child2');
        cy.get('div[data-testid="getChildNodes_all_3"]').contains('/sites/npmTestSite/home/testGetChildNodes/pagecontent/getChildNodesTest/filtered');

        cy.logout();
    });

    it('Verify filtered children returned', function () {
        cy.login();
        cy.visit('/jahia/page-composer/default/en/sites/npmTestSite/home/testGetChildNodes.html');
        cy.visit('/cms/render/default/en/sites/npmTestSite/home/testGetChildNodes.html');

        cy.get('div[data-testid="getChildNodes_filtered_1"]').contains('/sites/npmTestSite/home/testGetChildNodes/pagecontent/getChildNodesTest/filtered');
        cy.get('div[data-testid="getChildNodes_filtered_2"]').should('not.exist');
        cy.get('div[data-testid="getChildNodes_filtered_3"]').should('not.exist');

        cy.logout();
    });

    it('Verify limited children returned', function () {
        cy.login();
        cy.visit('/jahia/page-composer/default/en/sites/npmTestSite/home/testGetChildNodes.html');
        cy.visit('/cms/render/default/en/sites/npmTestSite/home/testGetChildNodes.html');

        cy.get('div[data-testid="getChildNodes_limit_1"]').contains('/sites/npmTestSite/home/testGetChildNodes/pagecontent/getChildNodesTest/child1');
        cy.get('div[data-testid="getChildNodes_limit_2"]').contains('/sites/npmTestSite/home/testGetChildNodes/pagecontent/getChildNodesTest/child2');
        cy.get('div[data-testid="getChildNodes_limit_3"]').should('not.exist');

        cy.logout();
    });
});
