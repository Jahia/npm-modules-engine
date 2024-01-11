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
    id: 'testJRender_taggedReact',
    target: 'npmExample:testJRender',
    templateName: 'taggedReact',
    displayName: 'test Render (tagged react)'
}
