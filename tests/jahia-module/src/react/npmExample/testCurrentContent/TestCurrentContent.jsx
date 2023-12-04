import React from 'react';
import {useServerContext} from '@jahia/server-jsx';

export const TestCurrentContent = () => {
    const {currentResource} = useServerContext();
    const mixins = currentResource.getNode().getMixinNodeTypes().map((e) => {return e.getName()}).join(",");
    const propMultiple = currentResource.getNode().getProperty('propMultiple').getValues().map((e) => {return e.getString()}).join(",");
    return (
        <>
            <h3>currentContent usages</h3>
            <div data-testid="currentContent_properties_jcr:title">jcr:title : {currentResource.getNode().getPropertyAsString('jcr:title')}</div>
            <div data-testid="currentContent_properties_prop1">prop1 : {currentResource.getNode().getPropertyAsString('prop1')}</div>
            <div data-testid="currentContent_properties_propMultiple">propMultiple : {propMultiple}</div>
            <div data-testid="currentContent_properties_propI18n">propI18n : {currentResource.getNode().getPropertyAsString('propI18n')}</div>
            <div data-testid="currentContent_properties_propRichText">
                <div>richtext : </div>
                <div dangerouslySetInnerHTML={{
                    __html: currentResource.getNode().getPropertyAsString('propRichText')
                }}/>
            </div>
            <div data-testid="currentContent_uuid">uuid : {currentResource.getNode().getIdentifier()}</div>
            <div data-testid="currentContent_name">name : {currentResource.getNode().getName()}</div>
            <div data-testid="currentContent_path">path : {currentResource.getNode().getPath()}</div>
            <div data-testid="currentContent_parent">parent : {currentResource.getNode().getParent().getPath()}</div>
            <div data-testid="currentContent_nodeType">nodeType : {currentResource.getNode().getPrimaryNodeTypeName()}</div>
            <div data-testid="currentContent_mixins">mixins : {mixins}</div>
            <hr/>
        </>
    )
}

TestCurrentContent.jahiaComponent = {
    id: 'testCurrentContent_react',
    target: 'npmExample:testCurrentContent',
    templateName: 'react',
    displayName: 'test currentContent (react)'
}