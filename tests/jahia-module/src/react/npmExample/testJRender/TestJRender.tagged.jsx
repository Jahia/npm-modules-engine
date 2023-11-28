import React from 'react';
import {useServerContext} from '@jahia/server-jsx';

export default () => {
    const {currentResource} = useServerContext();
    return (
        <div>
            display tags: {currentResource.getNode().getPropertyAsString('j:tagList')}
        </div>
    )
}