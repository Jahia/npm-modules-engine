import React from 'react';
import { useServerContext } from '@jahia/js-server-engine';

export const testHasPermission = () => {
    const {currentNode} = useServerContext();
    const hPerm = currentNode.hasPermission;
    return (
        <>
            <h3>Node has permission</h3>
            <div data-testid="currentNode_hasPermission">{currentNode.hasPermission('component-npmExampleMix_npmExampleComponent') ? 'true' : 'false'}</div>
            <h3>Node doesn't have permission</h3>
            <div data-testid="currentNode_hasNotPermission">{currentNode.hasPermission('fakePermission') ? 'true' : 'false'}</div>
        </>
    );
}

testHasPermission.jahiaComponent = {
    nodeType: 'npmExample:testHasPermission',
    name: 'default',
    displayName: 'test has permission',
    componentType: 'view'
}