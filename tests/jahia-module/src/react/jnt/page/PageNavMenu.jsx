import React from 'react';
import {useServerContext, jBuildNavMenu} from '@jahia/server-jsx';

export default () => {
    const {renderContext} = useServerContext();
    const baseNode = renderContext.getRequest().getParameter('baseline');
    const maxDepth = renderContext.getRequest().getParameter('maxDepth') ? parseInt(renderContext.getRequest().getParameter('maxDepth'), 10) : 10;
    const startLevel = renderContext.getRequest().getParameter('startLevel') ? parseInt(renderContext.getRequest().getParameter('startLevel'), 10) : 0;
    return (
        <div dangerouslySetInnerHTML={{
            __html: JSON.stringify(jBuildNavMenu(maxDepth, baseNode, 'menuComponent', startLevel))
        }}/>
    )
}