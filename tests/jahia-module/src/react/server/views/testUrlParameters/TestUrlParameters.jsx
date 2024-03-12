import React from 'react';
import { useServerContext } from '@jahia/js-server-engine';

export const TestUrlParameters = () => {
    const {renderContext} = useServerContext();
    const urlParam = renderContext.getRequest().getParameter('test');
    return (
        <>
            <h3>Url parameters</h3>
            <div data-testid="renderContext_urlParameters">{urlParam}</div>
        </>
    );
}

TestUrlParameters.jahiaComponent = {
    nodeType: 'npmExample:testUrlParameters',
    name: 'default',
    displayName: 'test url parameters',
    componentType: 'view'
}