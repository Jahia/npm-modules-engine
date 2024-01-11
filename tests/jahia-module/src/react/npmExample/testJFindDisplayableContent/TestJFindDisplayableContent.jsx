import React from 'react';
import {useServerContext} from '@jahia/js-server-engine';
import {render} from '@jahia/server-helpers';

export const TestJFindDisplayableContent = () => {
    const {currentResource, renderContext} = useServerContext();

    let displayableNodePath = '';
    const targetNodeRef = currentResource.getNode().hasProperty('target') ?
        currentResource.getNode().getProperty('target').getValue().getNode() :
        undefined;
    if (targetNodeRef) {
        const displayableNode = render.findDisplayableNode(targetNodeRef, renderContext, null);
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

TestJFindDisplayableContent.jahiaComponent = {
    id: 'testJFindDisplayableContent_react',
    target: 'npmExample:testJFindDisplayableContent',
    templateName: 'react',
    displayName: 'test jFindDisplayableContent (react)'
}
