import React from 'react';
import {JArea} from '@jahia/server-jsx';

export default () => {
    return (
        <div>
            <h2>jArea heper testing component</h2>

            <h2>Basic Area</h2>
            <div data-testid="basicArea">
                <JArea name={"basicArea"}/>
            </div>

            <h2>Area with allowed types</h2>
            <div data-testid="allowedTypesArea">
                <JArea name={"AllowedTypesArea"} allowedTypes={["jnt:event", "jnt:bigText"]}/>
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
        </div>
    )
}