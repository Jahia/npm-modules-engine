import React from 'react';
import { render } from '@jahia/server-helpers';
import {useServerContext} from "./ServerContext";

export default ({content, node, path, editable, advanceRenderingConfig, templateType, view, parameters}) => {
    const {renderContext, currentResource} = useServerContext()
    return (
        // todo we should find a way to strip this unwanted div here, check: https://stackoverflow.com/a/65033466
        <div dangerouslySetInnerHTML={{
            __html: render.render({
                content,
                node,
                path,
                editable,
                advanceRenderingConfig,
                templateType,
                view,
                parameters
            }, renderContext, currentResource)
        }} />
    );
}