import { API } from '../../utils/API'
import { addNode, createSite, deleteSite, getNodeTypes } from '@jahia/cypress'

describe('Check that components of a module are correctly registered', () => {
    const siteKey = 'registrationTestSite'

    const addSimplePage = (
        parentPathOrId: string,
        pageName: string,
        pageTitle: string,
        language: string,
        template,
        children = [],
    ) => {
        const variables = {
            parentPathOrId: parentPathOrId,
            name: pageName,
            title: pageTitle,
            primaryNodeType: 'jnt:page',
            properties: [
                { name: 'jcr:title', value: pageTitle, language: language },
                { name: 'j:templateName', type: 'STRING', value: template },
            ],
            children: children,
        }
        return addNode(variables)
    }

    before(() => {
        const fileName = 'engine-test-template-v1.0.0.tgz'
        API.installBundle(fileName).then((response) => {
            expect(response.status).to.eq(200)
            const bodyString = Cypress.Blob.arrayBufferToBinaryString(response.body)
            const body = JSON.parse(bodyString)
            expect(body.message).to.contains('successful')

            createSite(siteKey, {
                languages: 'en',
                templateSet: 'engine-test-template',
                locale: 'en',
                serverName: 'localhost',
            })
        })
    })

    it('Verify templates are registered', function () {
        cy.login()

        addSimplePage(`/sites/${siteKey}/home`, 'simple', 'Simple page', 'en', 'home', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/${siteKey}/home/simple/pagecontent`,
                name: 'simple-text',
                primaryNodeType: 'jnt:text',
                properties: [{ name: 'text', value: 'Main content text', language: 'en' }],
            })
            cy.visit(`/cms/render/default/en/sites/${siteKey}/home/simple.html`)
            cy.contains('Main content text')
        })
        addSimplePage(`/sites/${siteKey}/home`, 'two-columns', 'Two columns', 'en', '2-columns', [
            {
                name: 'main',
                primaryNodeType: 'jnt:contentList',
            },
            {
                name: 'right-column',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/${siteKey}/home/two-columns/main`,
                name: 'simple-text',
                primaryNodeType: 'jnt:text',
                properties: [{ name: 'text', value: 'Main content text', language: 'en' }],
            })
            addNode({
                parentPathOrId: `/sites/${siteKey}/home/two-columns/right-column`,
                name: 'right-text',
                primaryNodeType: 'jnt:text',
                properties: [{ name: 'text', value: 'Right text', language: 'en' }],
            })
            cy.visit(`/cms/render/default/en/sites/${siteKey}/home/two-columns.html`)

            cy.contains('Main content text')
            cy.contains('Right text')
        })
        cy.logout()
    })

    it('Verify nodeTypes and icons are registered', function () {
        cy.login()
        getNodeTypes({ includeTypes: ['etest:withTestMixin', 'etest:simple'] })
            .its('data.jcr.nodeTypes.nodes')
            .then((nodes) => {
                const simpleType = nodes.find((node) => node.name === 'etest:simple')
                expect(
                    simpleType.properties.filter((property) => property.name === 'prop1' || property.name === 'prop2')
                        .length,
                ).to.eq(2)
                expect(simpleType.icon).to.eq('/modules/engine-test-template/icons/etest_simple')
                const withTestMixinType = nodes.find((node) => node.name === 'etest:withTestMixin')
                expect(
                    withTestMixinType.properties.filter(
                        (property) => property.name === 'prop1' || property.name === 'prop2',
                    ).length,
                ).to.eq(2)
                expect(
                    withTestMixinType.supertypes.filter((superType) => superType.name === 'etestmix:testType').length,
                ).to.eq(1)
                expect(withTestMixinType.icon).to.eq('/modules/engine-test-template/icons/etest_withTestMixin')
            })
        cy.logout()
    })

    after('Clean', () => {
        deleteSite(siteKey)
        API.uninstallBundle('engine-test-template', '1.0.0')
    })
})
