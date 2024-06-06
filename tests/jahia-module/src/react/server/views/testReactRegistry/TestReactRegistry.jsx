import React from 'react';
import {server} from '@jahia/js-server-core';

export const TestReactRegistry = () => {
    const testAreasName = server.registry.get('view', 'jahia-npm-module-example_view_npmExample:testJAreas_default').name;
    const testReactRegistryName = server.registry.get('view', '@@@customid//for-testing@@@_view_npmExample:testReactRegistry_default').name;
    // We test non existant entries to make sure that the ProxyObject is not created by the RegistryHelper in this case
    const nonExistantComponent = server.registry.get('view', '@@@thisobjectdoesnotexist@@@');
    return (
        <>
            <div data-testid="standardViewRegistration">
                npmExample:testAreas view component name=[{testAreasName}]
            </div>
            <div data-testid="customViewRegistration">
                npmExample:testReactRegistry view component name=[{testReactRegistryName}]
            </div>
            <div data-testid="noRegistration">
                Non existing component=[{nonExistantComponent || 'null'}]
            </div>
        </>
    )
}

TestReactRegistry.jahiaComponent = {
    // we provide an id here to make sure the registration also works properly for this case, by it is not mandatory
    id: '@@@customid//for-testing@@@_view_npmExample:testReactRegistry_default',
    nodeType: 'npmExample:testReactRegistry',
    displayName: 'Test React registry',
    componentType: 'view'
}
