import React from 'react';
import {useServerContext} from '@jahia/server-jsx';

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
    id: 'testJRender_subReact',
    target: 'npmExample:testJRender',
    templateName: 'subReact',
    displayName: 'test Render (sub react)'
}