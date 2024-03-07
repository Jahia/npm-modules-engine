import React from 'react';
import {JArea, useServerContext} from '@jahia/js-server-engine';

export const TestJAreaColumns = () => {
    const {currentNode} = useServerContext();
    return (
        <div data-testid={`row-${currentNode.getName()}`}>
            <div data-testid={`${currentNode.getName()}-col-1`}>
                <JArea name={`${currentNode.getName()}-col-1`} areaAsSubNode={true}/>
            </div>
            <div data-testid={`${currentNode.getName()}-col-2`}>
                <JArea name={`${currentNode.getName()}-col-2`} areaAsSubNode={true}/>
            </div>
        </div>
    );
};

TestJAreaColumns.jahiaComponent = {
    nodeType: 'npmExample:testReactJAreaColumns',
    name: 'default',
    componentType: 'view'
};
