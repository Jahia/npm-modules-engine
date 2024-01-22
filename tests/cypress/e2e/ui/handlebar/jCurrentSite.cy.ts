import {addSimplePage} from '../../../utils/Utils';
import {addNode, deleteNode, enableModule, publishAndWaitJobEnding} from '@jahia/cypress';

describe('Test on jCurrentSite helper', () => {
    const defaultSiteInfos = {
        name: 'npmTestSite',
        path: '/sites/npmTestSite',
        parent: '/sites',
        nodeType: 'jnt:virtualsite',
        'property_j:installedModules': 'jahia-npm-module-example',
        'property_j:defaultLanguage': 'en',
        'property_j:mixLanguage': 'false',
        'property_j:title': 'npmTestSite',
        'property_j:inactiveLiveLanguages': '',
        'property_j:inactiveLanguages': '',
        'property_j:wcagCompliance': 'true',
        'property_j:mandatoryLanguages': '',
        'property_j:languages': 'en',
        'property_j:templatesSet': 'jahia-npm-module-example'
    };

    before('Create test page and contents', () => {
        addSimplePage('/sites/npmTestSite/home', 'testJCurrentSite', 'testJCurrentSite', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList'
            }
        ]).then(() => {
            addNode({
                parentPathOrId: '/sites/npmTestSite/home/testJCurrentSite/pagecontent',
                name: 'test',
                primaryNodeType: 'npmExample:testJCurrentSite'
            });

            publishAndWaitJobEnding('/sites/npmTestSite');
        });
    });

    after('Clean', () => {
        deleteNode('/sites/npmTestSite/home/testJCurrentSite');
        publishAndWaitJobEnding('/sites/npmTestSite');
    });

    it('Check jCurrentSite helper in preview', function () {
        cy.login();
        cy.visit('/cms/render/default/en/sites/npmTestSite/home/testJCurrentSite.html');
        testHelperResult(defaultSiteInfos);
        cy.logout();
    });

    it('Check jCurrentSite helper in edit', function () {
        cy.login();
        cy.visit('/jahia/jcontent/npmTestSite/en/pages/home/testJCurrentSite');
        cy.iframe('[data-sel-role="page-builder-frame-active"]', {timeout: 90000, log: true}).within(() => {
            testHelperResult(defaultSiteInfos);
        });
        cy.logout();
    });

    it('Check jCurrentSite helper in live', function () {
        cy.visit('/sites/npmTestSite/home/testJCurrentSite.html');
        testHelperResult(defaultSiteInfos);
    });

    it('Check jCurrentSite helper when enabling a new module on the site', function () {
        cy.login();
        enableModule('calendar', 'npmTestSite');
        cy.visit('/cms/render/default/en/sites/npmTestSite/home/testJCurrentSite.html');
        testHelperResult(defaultSiteInfos);
        testHelperResult({
            'property_j:installedModules': 'calendar'
        });
        cy.logout();
    });

    const testHelperResult = entries => {
        for (const key in entries) {
            if ({}.hasOwnProperty.call(entries, key)) {
                cy.get(`div[data-testid="currentSite_${key}"]`).should('contain', entries[key]);
            }
        }
    };
});
