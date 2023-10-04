import { addNode, addVanityUrl, deleteNode, publishAndWaitJobEnding } from '@jahia/cypress'
import { addSimplePage } from '../../utils/Utils'

describe('Test on url helper', () => {
    before('Create NPM test site', () => {
        cy.fixture('testData/image.jpg', 'binary').then((image) => {
            const blob = Cypress.Blob.binaryStringToBlob(image, 'image/jpeg')
            const file = new File([blob], 'image.jpg', { type: blob.type })
            cy.apollo({
                variables: {
                    path: '/sites/npmTestSite/files',
                    name: 'image.jpg',
                    mimeType: 'image/jpeg',
                    file: file,
                },
                mutationFile: 'graphql/jcrUploadFile.graphql',
            })
        })

        addSimplePage(`/sites/npmTestSite/home`, 'testUrl', 'testUrl', 'en', 'simple', [
            {
                name: 'pagecontent',
                primaryNodeType: 'jnt:contentList',
            },
        ]).then(() => {
            addNode({
                parentPathOrId: `/sites/npmTestSite/home/testUrl/pagecontent`,
                name: 'test',
                primaryNodeType: 'npmExample:testUrl',
                properties: [
                    {
                        name: 'linknode',
                        value: '/sites/npmTestSite/home/testPage',
                        type: 'WEAKREFERENCE',
                        language: 'en',
                    },
                    { name: 'image', value: '/sites/npmTestSite/files/image.jpg', type: 'WEAKREFERENCE' },
                ],
            })

            publishAndWaitJobEnding('/sites/npmTestSite')
        })
    })

    after('Clean', () => {
        deleteNode('/sites/npmTestSite/files/image.jpg')
        deleteNode('/sites/npmTestSite/home/testUrl')
        publishAndWaitJobEnding('/sites/npmTestSite')
    })

    it('Generated URLs should be correct', function () {
        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testUrl.html`)

        // check default workspace in preview
        let urls = [
            {
                dataTestId: 'image_reference',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/files/default/sites/npmTestSite/files/image.jpg',
            },
            {
                dataTestId: 'image_static_resource',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/modules/npm-module-example/images/goat.jpg',
            },
            {
                dataTestId: 'content_link',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/en/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_edit',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/edit/default/en/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_preview',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/en/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_live',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_language_fr',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/fr/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_parameters',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/en/sites/npmTestSite/home/testPage.html?param2=value2&param1=value1',
            },
            {
                dataTestId: 'action_url',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/en/sites/npmTestSite/home/testPage.myAction.do',
            },
        ]
        for (const url of urls) {
            cy.get(`div[data-testid="${url.dataTestId}"] ${url.tag}`)
                .should('have.attr', url.attribute)
                .should('include', url.expectedURL)
        }

        // check live workspace
        cy.visit(`/sites/npmTestSite/home/testUrl.html`)
        urls = [
            {
                dataTestId: 'image_reference',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/files/live/sites/npmTestSite/files/image.jpg',
            },
            {
                dataTestId: 'image_static_resource',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/modules/npm-module-example/images/goat.jpg',
            },
            {
                dataTestId: 'content_link',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_edit',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/edit/default/en/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_preview',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/en/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_live',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_language_fr',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/fr/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_parameters',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/sites/npmTestSite/home/testPage.html?param2=value2&param1=value1',
            },
            {
                dataTestId: 'action_url',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/sites/npmTestSite/home/testPage.myAction.do',
            },
        ]
        for (const url of urls) {
            cy.get(`div[data-testid="${url.dataTestId}"] ${url.tag}`)
                .should('have.attr', url.attribute)
                .should('include', url.expectedURL)
        }

        cy.logout()
    })

    it('Generated URLs should be correct with vanity', function () {
        // add a vanity url
        addVanityUrl('/sites/npmTestSite/home/testPage', 'en', '/vanityUrlTest')
        publishAndWaitJobEnding('/sites/npmTestSite/home/testPage')

        cy.login()
        cy.visit(`/cms/render/default/en/sites/npmTestSite/home/testUrl.html`)

        // check default workspace in preview
        let urls = [
            {
                dataTestId: 'image_reference',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/files/default/sites/npmTestSite/files/image.jpg',
            },
            {
                dataTestId: 'image_static_resource',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/modules/npm-module-example/images/goat.jpg',
            },
            {
                dataTestId: 'content_link',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/vanityUrlTest',
            },
            {
                dataTestId: 'content_link_mode_edit',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/edit/default/en/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_preview',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/vanityUrlTest',
            },
            { dataTestId: 'content_link_mode_live', tag: 'a', attribute: 'href', expectedURL: '/vanityUrlTest' },
            {
                dataTestId: 'content_link_language_fr',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/fr/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_parameters',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/vanityUrlTest?param2=value2&param1=value1',
            },
            {
                dataTestId: 'action_url',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/en/sites/npmTestSite/home/testPage.myAction.do',
            },
        ]
        for (const url of urls) {
            cy.get(`div[data-testid="${url.dataTestId}"] ${url.tag}`)
                .should('have.attr', url.attribute)
                .should('include', url.expectedURL)
        }

        // check live workspace
        cy.visit(`/sites/npmTestSite/home/testUrl.html`)
        urls = [
            {
                dataTestId: 'image_reference',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/files/live/sites/npmTestSite/files/image.jpg',
            },
            {
                dataTestId: 'image_static_resource',
                tag: 'img',
                attribute: 'src',
                expectedURL: '/modules/npm-module-example/images/goat.jpg',
            },
            { dataTestId: 'content_link', tag: 'a', attribute: 'href', expectedURL: '/vanityUrlTest' },
            {
                dataTestId: 'content_link_mode_edit',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/edit/default/en/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_mode_preview',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/cms/render/default/vanityUrlTest',
            },
            { dataTestId: 'content_link_mode_live', tag: 'a', attribute: 'href', expectedURL: '/vanityUrlTest' },
            {
                dataTestId: 'content_link_language_fr',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/fr/sites/npmTestSite/home/testPage.html',
            },
            {
                dataTestId: 'content_link_parameters',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/vanityUrlTest?param2=value2&param1=value1',
            },
            {
                dataTestId: 'action_url',
                tag: 'a',
                attribute: 'href',
                expectedURL: '/sites/npmTestSite/home/testPage.myAction.do',
            },
        ]
        for (const url of urls) {
            cy.get(`div[data-testid="${url.dataTestId}"] ${url.tag}`)
                .should('have.attr', url.attribute)
                .should('include', url.expectedURL)
        }

        cy.logout()
    })
})
