import {addNode, deleteNode, publishAndWaitJobEnding} from '@jahia/cypress';
import {addSimplePage} from '../../../utils/Utils';

describe('Test on ctx injected in views', () => {
    before('Create test page and contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testCtx', 'testCtx', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testCtx/pagecontent',
                name: 'test',
                primaryNodeType: 'npmExample:testCtx'
            });

            publishAndWaitJobEnding('/sites/npmTestSite');
        });
    });

    after('Clean', () => {
        deleteNode('/sites/npmTestSite/home/testCtx');
        publishAndWaitJobEnding('/sites/npmTestSite');
    });

    const testCtxMode = modeValue => {
        const mode = {
            value: 'value',
            isEdit: 'edit',
            isPreview: 'preview',
            isLive: 'live',
            isFrame: 'frame'
        };
        for (const key in mode) {
            if ({}.hasOwnProperty.call(mode, key)) {
                switch (key) {
                    case 'value':
                        cy.get(`li[data-testid="ctx_mode_${key}"]`).should('contain', `${modeValue}`);
                        break;
                    default:
                        cy.get(`li[data-testid="ctx_mode_${key}"]`).should('contain', `${mode[key] === modeValue}`);
                        break;
                }
            }
        }
    };

    const testCtxEntries = entries => {
        for (const key in entries) {
            if (key === 'mode') {
                testCtxMode(entries[key]);
            } else {
                cy.get(`div[data-testid="ctx_${key}"]`).should('contain', entries[key]);
            }
        }
    };

    it('test ctx in preview', function () {
        cy.login();
        cy.visit('/cms/render/default/en/sites/npmTestSite/home/testCtx.html');
        testCtxEntries({
            isAjaxRequest: 'false',
            isLoggedIn: 'true',
            readOnlyStatus: 'OFF',
            mode: 'preview',
            workspace: 'default',
            contentLanguage: 'en',
            uiLanguage: 'en',
            currentModule: '/modules/jahia-npm-module-example'
        });
        cy.logout();
    });

    it('test ctx in edit', function () {
        cy.login();
        cy.visit('/jahia/jcontent/npmTestSite/en/pages/home/testCtx');
        cy.iframe('[data-sel-role="page-builder-frame-active"]', {timeout: 90000, log: true}).within(() => {
            testCtxEntries({
                isAjaxRequest: 'false',
                isLoggedIn: 'true',
                readOnlyStatus: 'OFF',
                mode: 'edit',
                workspace: 'default',
                contentLanguage: 'en',
                uiLanguage: 'en',
                currentModule: '/modules/jahia-npm-module-example'
            });
        });
        cy.logout();
    });

    it('test ctx in live guest', function () {
        cy.visit('/sites/npmTestSite/home/testCtx.html');
        testCtxEntries({
            isAjaxRequest: 'false',
            isLoggedIn: 'false',
            readOnlyStatus: 'OFF',
            mode: 'live',
            workspace: 'live',
            contentLanguage: 'en',
            uiLanguage: 'en',
            currentModule: '/modules/jahia-npm-module-example'
        });
    });

    it('test ctx in live logged', function () {
        cy.login();
        cy.visit('/sites/npmTestSite/home/testCtx.html');
        testCtxEntries({
            isAjaxRequest: 'false',
            isLoggedIn: 'true',
            readOnlyStatus: 'OFF',
            mode: 'live',
            workspace: 'live',
            contentLanguage: 'en',
            uiLanguage: 'en',
            currentModule: '/modules/jahia-npm-module-example'
        });
        cy.logout();
    });

    it('test ctx in ajax rendered content', function () {
        cy.visit('/sites/npmTestSite/home/testCtx/pagecontent/test.html.ajax');
        testCtxEntries({
            isAjaxRequest: 'true',
            isLoggedIn: 'false',
            readOnlyStatus: 'OFF',
            mode: 'live',
            workspace: 'live',
            contentLanguage: 'en',
            uiLanguage: 'en',
            currentModule: '/modules/jahia-npm-module-example'
        });
    });
});
