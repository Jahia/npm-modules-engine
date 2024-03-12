import React from 'react';
import { useServerContext } from '@jahia/js-server-engine';

export const TestCurrentUser = () => {
    const {renderContext} = useServerContext();
    const currentUser = renderContext.getUser();
    return (
        <>
            <h3>Current user infos</h3>
            <div data-testid="currentUser_name">{currentUser.getName()}</div>
            <div data-testid="currentUser_isRoot">{currentUser.isRoot() ? 'true' : 'false'}</div>
        </>
    );
}

TestCurrentUser.jahiaComponent = {
    nodeType: 'npmExample:testCurrentUser',
    name: 'default',
    displayName: 'test current user',
    componentType: 'view'
}