import {addNode} from '@jahia/cypress';
import {addSimplePage} from '../../utils/Utils';

describe('Error handling test during JS view execution', () => {
    before('Create test contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testCrashingView', 'testCrashingView', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testCrashingView/pagecontent',
                name: 'test',
                primaryNodeType: 'npmExample:testCrashingView'
            });
        });

        addSimplePage('/sites/npmTestSite/home', 'testCrashingViewReact', 'testCrashingViewReact', 'en', 'simpleReact', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testCrashingViewReact/pagecontent',
                name: 'test',
                primaryNodeType: 'npmExample:testCrashingView',
                mixins: ['jmix:renderable'],
                properties: [
                    {name: 'j:view', value: 'react'}
                ]
            });
        });
    });

    [{
        case: 'HandleBar',
        pagePath: '/home/testCrashingView',
        viewKey: 'default',
        viewPath: '/jahia-npm-module-example_components/npmExample/testCrashingView/testCrashingView.hbs',
        errorMessage: '/path/not/exists'
    }, {
        case: 'React',
        pagePath: '/home/testCrashingViewReact',
        viewKey: 'react',
        viewPath: '/jahia-npm-module-example_view_npmExample:testCrashingView_react',
        errorMessage: 'Property not_existing_property not found on node: /sites/npmTestSite/home/testCrashingViewReact/pagecontent/test'
    }].forEach(testConfig => {
        it(`${testConfig.case}: Check error handling when rendering a crashing JS view`, () => {
            cy.login();
            cy.visit(`/jahia/jcontent/npmTestSite/en/pages${testConfig.pagePath}`);
            cy.iframe('[data-sel-role="page-builder-frame-active"]', {timeout: 90000, log: true}).within(() => {
                // Check that the error information is correctly displayed
                cy.get('[data-sel-role="renderingFailureViewKey"]').should('contain', testConfig.viewKey);
                cy.get('[data-sel-role="renderingFailureViewPath"]').should('contain', testConfig.viewPath);
                cy.get('[data-sel-role="renderingFailureErrorMessage"]').should('contain', testConfig.errorMessage);
                // Check that by default the full error is not displayed
                cy.get('[data-sel-role="renderingFailureFullError"]').should('not.be.visible');
                // Click on the button to display the full error
                cy.get('button[data-sel-role="renderingFailureToggleFullError"]').click();
                cy.get('[data-sel-role="renderingFailureFullError"]').should('be.visible');
                // Click again on the button to hide the full error
                cy.get('button[data-sel-role="renderingFailureToggleFullError"]').click();
                cy.get('[data-sel-role="renderingFailureFullError"]').should('not.be.visible');
            });
            cy.logout();
        });
    });

    afterEach('Logout', () => cy.logout());
});
