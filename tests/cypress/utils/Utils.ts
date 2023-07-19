/*  eslint-disable @typescript-eslint/no-explicit-any */
import { addNode } from '@jahia/cypress'
export const addSimplePage = (
    parentPathOrId: string,
    pageName: string,
    pageTitle: string,
    language: string,
    template = 'home',
    children = [],
): any => {
    const variables = {
        parentPathOrId: parentPathOrId,
        name: pageName,
        title: pageTitle,
        primaryNodeType: 'jnt:page',
        template: 'home',
        properties: [
            { name: 'jcr:title', value: pageTitle, language: language },
            { name: 'j:templateName', type: 'STRING', value: template },
        ],
        children: children,
    }
    return addNode(variables)
}
