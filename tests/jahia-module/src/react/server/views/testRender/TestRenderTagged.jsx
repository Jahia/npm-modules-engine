import React from 'react';
import {useServerContext} from '@jahia/js-server-core';

export const TestRenderTagged = () => {
    const {currentResource} = useServerContext();
    return (
        <div>
            display tags: {currentResource.getNode().getPropertyAsString('j:tagList')}
        </div>
    )
}

TestRenderTagged.jahiaComponent = {
    nodeType: 'npmExample:testRender',
    name: 'tagged',
    displayName: 'test Render (tagged react)',
    componentType: 'view'
}
