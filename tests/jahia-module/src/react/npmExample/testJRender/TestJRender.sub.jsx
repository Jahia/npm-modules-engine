import React from 'react';
import {useServerContext} from '@jahia/js-server-engine';

export const TestJRenderSub = () => {
    const {currentResource} = useServerContext();
    return (
        <div>
            Sub view {currentResource.getNode().getName()} <br/>
            prop1={currentResource.getNode().getPropertyAsString('prop1')}
        </div>
    )
}

TestJRenderSub.jahiaComponent = {
    nodeType: 'npmExample:testJRender',
    name: 'subReact',
    displayName: 'test Render (sub react)',
    componentType: 'view'
}
