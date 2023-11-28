import React from 'react';
import {useServerContext} from '@jahia/server-jsx';

export default () => {
    const {currentResource} = useServerContext();
    return (
        <div>
            Sub view {currentResource.getNode().getName()} <br/>
            prop1={currentResource.getNode().getPropertyAsString('prop1')}
        </div>
    )
}