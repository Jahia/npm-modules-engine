import React from 'react';
import {useServerContext} from '@jahia/js-server-core';

export const TestCrashingView = () => {
    const {currentNode} = useServerContext();
    currentNode.getProperty('not_existing_property');

    return (
        <>
            <p>This view is expected to crash</p>
        </>
    )
}

TestCrashingView.jahiaComponent = {
    nodeType: 'npmExample:testCrashingView',
    name: 'default',
    displayName: 'test crashing view',
    componentType: 'view'
}
