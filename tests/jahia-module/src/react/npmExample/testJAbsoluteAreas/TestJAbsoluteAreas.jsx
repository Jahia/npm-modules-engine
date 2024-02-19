import React from 'react';
import {JAbsoluteArea} from '@jahia/js-server-engine';

export const TestJAbsoluteAreas = () => {
    return (
        <>
            <h2>React JAbsoluteArea test component</h2>

            <h2>Basic Area</h2>
            <div data-testid="basicArea">
                <JAbsoluteArea name={"basicArea"} level={1}/>
            </div>

            <h2>Area with allowed types</h2>
            <div data-testid="allowedTypesArea">
                <JAbsoluteArea name={"allowedTypesArea"} allowedTypes={["jnt:event", "jnt:bigText"]} level={1}/>
            </div>

            <h2>Area with number of items</h2>
            <div data-testid="numberOfItemsArea">
                <JAbsoluteArea name={"numberOfItemsArea"} numberOfItems={2} level={1}/>
            </div>

            <h2>Area with areaView</h2>
            <div data-testid="areaViewArea">
                <JAbsoluteArea name={"areaViewArea"} areaView={"dropdown"} level={1}/>
            </div>

            <h2>Area with subNodesView</h2>
            <div data-testid="subNodesViewArea">
                <JAbsoluteArea name={"subNodesViewArea"} subNodesView={"link"} level={1}/>
            </div>

            <h2>Area with path</h2>
            <div data-testid="pathArea">
                <JAbsoluteArea path={"basicArea/subLevel"} level={1}/>
            </div>

            <h2>Absolute Area with home page content</h2>
            <div data-testid="absoluteArea">
                <JAbsoluteArea name="pagecontent" />
            </div>

            <h2>Non editable area </h2>
            <div data-testid="nonEditableArea">
                <JAbsoluteArea name="nonEditable" editable={false} level={1}/>
            </div>

            <h2>Absolute area level </h2>
            <div data-testid="absoluteAreaLevel">
                <JAbsoluteArea name="pagecontent" level={0}/>
            </div>

            <h2>Area type</h2>
            <div data-testid="areaType">
                <JAbsoluteArea name="areaType" areaType="npmExample:testReactJAreaColumns" level={1}/>
            </div>

            <h2>Limited absolute area editing</h2>
            <div data-testid="limitedAbsoluteAreaEdit">
                <JAbsoluteArea name="pagecontent" limitedAbsoluteAreaEdit={false}/>
            </div>

        </>
    )
}

TestJAbsoluteAreas.jahiaComponent = {
    id: 'testJAbsoluteAreas_react',
    nodeType: 'npmExample:testJAbsoluteAreas',
    name: 'react',
    displayName: 'Test Absolute Areas (react)',
    componentType: 'view'
}
