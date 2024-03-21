import React from 'react';
import {server, useServerContext} from '@jahia/js-server-engine';

export const TestFindDisplayableContent = () => {
    const {currentResource, renderContext} = useServerContext();

    let displayableNodePath = '';
    const targetNodeRef = currentResource.getNode().hasProperty('target') ?
        currentResource.getNode().getProperty('target').getValue().getNode() :
        undefined;
    if (targetNodeRef) {
        const displayableNode = server.render.findDisplayableNode(targetNodeRef, renderContext, null);
        if (displayableNode) {
            displayableNodePath = displayableNode.getPath();
        }
    }
    return (
        <>
            <h2>Test findDisplayableNode helper</h2>

            <p data-testid="displayableContent">
                Found displayable content: {displayableNodePath}
            </p>
        </>
    )
}

TestFindDisplayableContent.jahiaComponent = {
    nodeType: 'npmExample:testJFindDisplayableContent',
    name: 'react',
    displayName: 'test jFindDisplayableContent (react)',
    componentType: 'view'
}
