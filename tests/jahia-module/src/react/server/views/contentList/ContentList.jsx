import React from 'react';
import {useServerContext} from '@jahia/js-server-core';

export const ContentList = () => {
    const {currentResource} = useServerContext();
    return (
        <>
            <h3>React Parameter view {currentResource.getNode().getName()}</h3>
            <div data-testid="areaParam-string1">
                stringParam1={currentResource.getModuleParams().get('stringParam1')}
            </div>
            <div data-testid="areaParam-string2">
                stringParam2={currentResource.getModuleParams().get('stringParam2')}
            </div>
        </>
    )
}

ContentList.jahiaComponent = {
    nodeType: 'jnt:contentList',
    name: 'parametersReact',
    displayName: 'Test Area with parameters (React)',
    componentType: 'view'
}
