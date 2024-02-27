import React from 'react';
import {getChildNodes, JAddContentButtons, useServerContext} from '@jahia/js-server-engine';

export const TestGetChildNodes = () => {
    const {currentNode} = useServerContext();
    const allChildren = getChildNodes(currentNode)
    const filteredChildren = getChildNodes(currentNode, 100, (child) => {
        return child.getName() === 'filtered'
    }) ;
    const limitChildren = getChildNodes(currentNode, 2);

    return (
        <>
            <h3>getChildNodes usages</h3>
            <h2>All Children</h2>
            <div data-testid={`getChildNodes_all`}>
                {allChildren && allChildren.map(function (child, i) {
                    return <div data-testid={`getChildNodes_all_${i + 1}`} key={i}>{child.getPath()}</div>
                })}
            </div>

            <h2>Filtered Children</h2>
            <div data-testid={`getChildNodes_filtered`}>
                {filteredChildren && filteredChildren.map(function (child, i) {
                    return <div data-testid={`getChildNodes_filtered_${i + 1}`} key={i}>{child.getPath()}</div>
                })}
            </div>

            <h2>Limit Children</h2>
            <div data-testid={`getChildNodes_limit`}>
                {limitChildren && limitChildren.map(function (child, i) {
                    return <div data-testid={`getChildNodes_limit_${i + 1}`} key={i}>{child.getPath()}</div>
                })}
            </div>
            <JAddContentButtons/>
        </>
    )
}

TestGetChildNodes.jahiaComponent = {
    nodeType: 'npmExample:testGetChildNodes',
    name: 'default',
    displayName: 'test getChildNodes',
    componentType: 'view'
}
