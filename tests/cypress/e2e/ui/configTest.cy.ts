import {addNode, deleteNode, publishAndWaitJobEnding} from '@jahia/cypress'
import {addSimplePage} from '../../utils/Utils'

describe('Test OSGi configuration in views', () => {
    before('Create NPM test page', () => {
        addSimplePage(`/sites/npmTestSite/home`, 'testConfig', 'testConfig', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testConfig/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testConfig',
            })

            publishAndWaitJobEnding('/sites/npmTestSite')
        })
    })

    after('Clean', () => {
        deleteNode('/sites/npmTestSite/home/testConfig')
        publishAndWaitJobEnding('/sites/npmTestSite')
    })

    const testConfigEntries = () => {
        cy.get(`p[data-testid="configKey1"]`).should('contain', 'configKey1=configValue1')
        cy.get(`p[data-testid="configValues.configKey1"]`).should('contain', 'configValues.configKey1=configValue1')
        cy.get(`p[data-testid="configValues.configKey2"]`).should('contain', 'configValues.configKey2=configValue2')
        cy.get(`p[data-testid="defaultFactoryConfigs.configKey1"]`).should(
            'contain',
            'defaultFactoryConfigs.configKey1=configValue1',
        )
        cy.get(`p[data-testid="defaultFactoryConfigs.configKey2"]`).should(
            'contain',
            'defaultFactoryConfigs.configKey2=configValue2',
        )
        cy.get(`p[data-testid="testModuleFactoryIdentifiers"]`).should(
            'contain',
            'testModuleFactoryIdentifiers=default,id1,id2',
        )
        cy.get(`div[data-testid="complexObject_metadata.name"]`).should('contain', 'metadata.name: my-app')
    }

    it('test config in preview', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testConfig.html`)
        testConfigEntries()
        cy.logout()
    })

    it('test config in edit', function () {
        cy.login()
        cy.visit(`/jahia/jcontent/npmTestSite/en/pages/home/testConfig`)
        cy.iframe('[data-sel-role="page-builder-frame-active"]', {timeout: 90000, log: true}).within(() => {
            testConfigEntries()
        })
        cy.logout()
    })

    it('test config in live guest', function () {
        cy.visit(`/sites/npmTestSite/home/testConfig.html`)
        testConfigEntries()
    })

    it('test config in live logged', function () {
        cy.login()
        cy.visit(`/sites/npmTestSite/home/testConfig.html`)
        testConfigEntries()
        cy.logout()
    })

    it('test config in ajax rendered content', function () {
        cy.visit(`/sites/npmTestSite/home/testConfig/pagecontent/test.html.ajax`)
        testConfigEntries()
    })
})
