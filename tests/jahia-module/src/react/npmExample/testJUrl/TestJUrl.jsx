import React from 'react';
import {jAddCacheDependency, jUrl, useServerContext} from '@jahia/js-server-engine';

export const TestJUrl = () => {
    const {currentResource, renderContext} = useServerContext();
    const currentModule = renderContext.getURLGenerator().getCurrentModule();

    const imageNodeRef = currentResource.getNode().hasProperty('image') ?
        currentResource.getNode().getProperty('image').getValue().getNode() :
        undefined;
    if (imageNodeRef) {
        jAddCacheDependency({path: imageNodeRef.getPath()})
    }

    const linkNodeRef = currentResource.getNode().hasProperty('linknode') ?
        currentResource.getNode().getProperty('linknode').getValue().getNode() :
        undefined;
    if (linkNodeRef) {
        jAddCacheDependency({path: linkNodeRef.getPath()})
    }

    return (
        <>
            <h3>Url helper testing component</h3>

            {imageNodeRef &&
                <div data-testid="image_reference">
                    <img height="150" src={jUrl({path: imageNodeRef.getPath()})} alt="image"/>
                </div>}

            <div data-testid="image_static_resource">
                <img height="150" src={jUrl({value: currentModule + '/images/goat.jpg'})} alt="goat"/>
            </div>

            {linkNodeRef &&
                <>
                    <div data-testid="content_link">
                        <a href={jUrl({path: linkNodeRef.getPath()})}>content link - current context</a>
                    </div>
                    <div data-testid="content_link_mode_edit">
                        <a href={jUrl({path: linkNodeRef.getPath(), mode:'edit'})}>content link - edit</a>
                    </div>
                    <div data-testid="content_link_mode_preview">
                        <a href={jUrl({path: linkNodeRef.getPath(), mode:'preview'})}>content link - preview</a>
                    </div>
                    <div data-testid="content_link_mode_live">
                        <a href={jUrl({path: linkNodeRef.getPath(), mode:'live'})}>content link - live</a>
                    </div>
                    <div data-testid="content_link_language_fr">
                        <a href={jUrl({path: linkNodeRef.getPath(), language:'fr'})}>content link - FR</a>
                    </div>
                    <div data-testid="content_link_parameters">
                        <a href={jUrl({path: linkNodeRef.getPath(), parameters: {param1: 'value1', param2: 'value2'}})}>content link - parameters</a>
                    </div>
                    <div data-testid="action_url">
                        <a href={jUrl({path: linkNodeRef.getPath(), extension:'.myAction.do'})}>action link</a>
                    </div>
                </>}

            <div data-testid="absolute_external_link">
                <a href={jUrl({value: 'https://www.google.com'})}>External absolute link</a>
            </div>
            <div data-testid="path_not_exists">
                <a href={jUrl({path: '/sites/mySiteNotExists/home'})}>Using a path of node that does not exists</a>
            </div>
        </>
    )
}

TestJUrl.jahiaComponent = {
    id: 'testJUrl_react',
    target: 'npmExample:testJUrl',
    templateName: 'react',
    displayName: 'test jUrl (react)'
}
