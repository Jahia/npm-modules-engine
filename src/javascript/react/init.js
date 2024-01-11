import {gql, registry} from '@jahia/server-helpers';
import React from 'react';
import JRender from '@jahia/js-server-engine/react/JRender';
import JAddContentButtons from '@jahia/js-server-engine/react/JAddContentButtons';
import jBuildNavMenu from '@jahia/js-server-engine/react/jBuildNavMenu';
import jAddCacheDependency from '@jahia/js-server-engine/react/jAddCacheDependency';
import jUrl from '@jahia/js-server-engine/react/jUrl';
import ReactDOMServer from 'react-dom/server';
import styledJsx from 'styled-jsx/style';
import {ServerContextProvider, useServerContext} from '@jahia/js-server-engine/react/ServerContext';
import {createStyleRegistry, StyleRegistry} from 'styled-jsx';
import JArea from '@jahia/js-server-engine/react/JArea';
import JAddResources from '@jahia/js-server-engine/react/JAddResources';
import {registerJahiaComponents} from '@jahia/js-server-engine/react/register';
import getNodeProps from '@jahia/js-server-engine/utils/getNodeProps';
import getChildNodes from '@jahia/js-server-engine/utils/getChildNodes';

export default () => {
    // Hack to expose react to other modules
    registry.add('module', 'react', {
        exports: React
    });
    registry.add('module', 'styled-jsx', {
        exports: styledJsx
    });

    // Expose Jahia JSX functions/components
    registry.add('module', 'jahia-js-server-engine', {
        exports: {
            JRender,
            JArea,
            JAddContentButtons,
            JAddResources,
            jBuildNavMenu,
            jAddCacheDependency,
            jUrl,
            getNodeProps,
            getChildNodes,
            registerJahiaComponents,
            useServerContext,
            useQuery: ({query, variables, operationName}) => {
                const {renderContext} = useServerContext();
                return gql.executeQuerySync({query, variables, operationName, renderContext});
            }
        }
    });

    registry.add('view', 'react', {
        viewRenderer: 'react'
    });

    registry.add('viewRenderer', 'react', {
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
