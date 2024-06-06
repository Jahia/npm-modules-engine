import React from 'react';
import {buildUrl, server, useServerContext} from '@jahia/js-server-core';

export const TestUrl = () => {
    const {currentResource, renderContext} = useServerContext();
    const currentModule = renderContext.getURLGenerator().getCurrentModule();

    const imageNodeRef = currentResource.getNode().hasProperty('image') ?
        currentResource.getNode().getProperty('image').getValue().getNode() :
        undefined;
    if (imageNodeRef) {
        server.render.addCacheDependency({path: imageNodeRef.getPath()}, renderContext)
    }

    const linkNodeRef = currentResource.getNode().hasProperty('linknode') ?
        currentResource.getNode().getProperty('linknode').getValue().getNode() :
        undefined;
    if (linkNodeRef) {
        server.render.addCacheDependency({path: linkNodeRef.getPath()}, renderContext)
    }

    return (
        <>
            <h3>Url helper testing component</h3>

            {imageNodeRef &&
                <div data-testid="image_reference">
                    <img height="150" src={buildUrl({path: imageNodeRef.getPath()}, renderContext, currentResource)} alt="image"/>
                </div>}

            <div data-testid="image_static_resource">
                <img height="150" src={buildUrl({value: currentModule + '/images/goat.jpg'}, renderContext, currentResource)} alt="goat"/>
            </div>

            {linkNodeRef &&
                <>
                    <div data-testid="content_link">
                        <a href={buildUrl({path: linkNodeRef.getPath()}, renderContext, currentResource)}>content link - current context</a>
                    </div>
                    <div data-testid="content_link_mode_edit">
                        <a href={buildUrl({path: linkNodeRef.getPath(), mode:'edit'}, renderContext, currentResource)}>content link - edit</a>
                    </div>
                    <div data-testid="content_link_mode_preview">
                        <a href={buildUrl({path: linkNodeRef.getPath(), mode:'preview'}, renderContext, currentResource)}>content link - preview</a>
                    </div>
                    <div data-testid="content_link_mode_live">
                        <a href={buildUrl({path: linkNodeRef.getPath(), mode:'live'}, renderContext, currentResource)}>content link - live</a>
                    </div>
                    <div data-testid="content_link_language_fr">
                        <a href={buildUrl({path: linkNodeRef.getPath(), language:'fr'}, renderContext, currentResource)}>content link - FR</a>
                    </div>
                    <div data-testid="content_link_parameters">
                        <a href={buildUrl({path: linkNodeRef.getPath(), parameters: {param1: 'value1', param2: 'value2'}}, renderContext, currentResource)}>content link - parameters</a>
                    </div>
                    <div data-testid="action_url">
                        <a href={buildUrl({path: linkNodeRef.getPath(), extension:'.myAction.do'}, renderContext, currentResource)}>action link</a>
                    </div>
                </>}

            <div data-testid="absolute_external_link">
                <a href={buildUrl({value: 'https://www.google.com'}, renderContext, currentResource)}>External absolute link</a>
            </div>
            <div data-testid="path_not_exists">
                <a href={buildUrl({path: '/sites/mySiteNotExists/home'}, renderContext, currentResource)}>Using a path of node that does not exists</a>
            </div>
            <div data-testid="no_weakref">
                <a href={buildUrl({path: null})}>No weakref</a>
            </div>
        </>
    )
}

TestUrl.jahiaComponent = {
    nodeType: 'npmExample:testJUrl',
    name: 'default',
    displayName: 'test jUrl (react)',
    componentType: 'view'
}
