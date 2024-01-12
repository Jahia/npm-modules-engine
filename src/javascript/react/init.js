import {server} from '@jahia/js-server-engine-private';
import React from 'react';
import ReactDOMServer from 'react-dom/server';
import {createStyleRegistry, StyleRegistry} from 'styled-jsx';
import {ServerContextProvider} from "@jahia/js-server-engine";

export default () => {
    server.registry.add('view', 'react', {
        viewRenderer: 'react'
    });

    server.registry.add('viewRenderer', 'react', {
        render: (currentResource, renderContext, view) => {
            const id = 'reactTarget' + Math.floor(Math.random() * 100000000);
            const props = {
                id,
                currentNode: {
                    identifier: currentResource.getNode().getIdentifier(),
                    path: currentResource.getNode().getPath()
                },
                currentLocale: currentResource.getLocale().toString(),
                mainNode: {
                    identifier: renderContext.getMainResource().getNode().getIdentifier(),
                    path: renderContext.getMainResource().getNode().getPath()
                },
                user: renderContext.getUser().getUsername(),
                editMode: renderContext.isEditMode(),
                workspace: renderContext.getWorkspace(),
                uiLocale: renderContext.getUILocale().toString(),
                view: view.key,
                bundle: view.bundle.getSymbolicName()
            };

            // SSR
            const styleRegistry = createStyleRegistry();
            const currentNode = currentResource.getNode();
            const mainNode = renderContext.getMainResource().getNode();
            const element = React.createElement(StyleRegistry, {registry: styleRegistry}, React.createElement(ServerContextProvider, {renderContext, currentResource, currentNode, mainNode}, React.createElement(view.component, {...props})));
            const renderedElement = ReactDOMServer.renderToString(element);
            const styles = ReactDOMServer.renderToStaticMarkup(styleRegistry.styles());
            const stylesResource = styles ? `<jahia:resource type="inline" key="styles${id}">${styles}</jahia:resource>` : '';
            if (currentResource.getContextConfiguration() === 'page') {
                return `<html>${renderedElement}${stylesResource}</html>`;
            }

            return `${renderedElement}${stylesResource}`;
        }
    });
};
