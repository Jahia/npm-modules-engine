import React from 'react';
import {JArea} from '@jahia/js-server-engine';

export const TestJAreas = () => {
    return (
        <>
            <h2>React JArea test component</h2>

            <h2>Basic Area</h2>
            <div data-testid="basicArea">
                <JArea name={"basicArea"}/>
            </div>

            <h2>Area with allowed types</h2>
            <div data-testid="allowedTypesArea">
                <JArea name={"allowedTypesArea"} allowedTypes={["jnt:event", "jnt:bigText"]}/>
            </div>

            <h2>Area with number of items</h2>
            <div data-testid="numberOfItemsArea">
                <JArea name={"numberOfItemsArea"} numberOfItems={2}/>
            </div>

            <h2>Area with areaView</h2>
            <div data-testid="areaViewArea">
                <JArea name={"areaViewArea"} areaView={"dropdown"}/>
            </div>

            <h2>Area with subNodesView</h2>
            <div data-testid="subNodesViewArea">
                <JArea name={"subNodesViewArea"} subNodesView={"link"}/>
            </div>

            <h2>Area with path</h2>
            <div data-testid="pathArea">
                <JArea path={"basicArea/subLevel"}/>
            </div>

            <h2>Non editable area </h2>
            <div data-testid="nonEditableArea">
                <JArea name="nonEditable" editable="false"/>
            </div>

            <h2>Area as sub node </h2>
            <div data-testid="areaAsSubNode">
                <JArea name="areaAsSubNode" areaAsSubNode={true}/>
            </div>

            <h2>Area type</h2>
            <div data-testid="areaType">
                <JArea name="areaType" areaType="npmExample:testReactJAreaColumns"/>
            </div>

            <h2>Area parameters</h2>
            <div data-testid="areaParameters">
                <JArea name="areaParameters" areaView="parametersReact" parameters={{ "stringParam1": "stringValue1", "stringParam2": "stringValue2"}} />
            </div>

        </>
    )
}

TestJAreas.jahiaComponent = {
    nodeType: 'npmExample:testJAreas',
    name: 'react',
    displayName: 'test Areas (react)',
    componentType: 'view'
}
