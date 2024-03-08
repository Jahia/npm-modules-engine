import React from 'react';
import {useServerContext} from '@jahia/js-server-engine';

export const TestJRenderTagged = () => {
    const {currentResource} = useServerContext();
    return (
        <div>
            display tags: {currentResource.getNode().getPropertyAsString('j:tagList')}
        </div>
    )
}

TestJRenderTagged.jahiaComponent = {
    nodeType: 'npmExample:testJRender',
    name: 'taggedReact',
    displayName: 'test Render (tagged react)',
    componentType: 'view'
}
