import React from 'react';
import {useServerContext} from '@jahia/js-server-core';

export const PageMenuComponent = () => {
    const {currentResource} = useServerContext();
    return currentResource.getNode().getPath();
}

PageMenuComponent.jahiaComponent = {
    nodeType: 'jnt:page',
    name: 'menuComponent',
    componentType: 'view',
    properties: {
        'type': 'menuItem'
    }
}
