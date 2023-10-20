import {registry} from '@jahia/server-helpers';
import React from 'react';
import ReactDOMServer from 'react-dom/server';
import {flushToHTML} from 'styled-jsx/server';
import styledJsx from 'styled-jsx/style';
import {getMarkupFromTree} from '@apollo/client/react/ssr';
import {getClient} from '../apollo/client';

function getEncodedData(data) {
    // eslint-disable-next-line no-useless-escape
    return JSON.stringify(data).replace(/[\u00A0-\u9999<>\&";]/g, i => '&#' + i.charCodeAt(0) + ';');
}

export default () => {
    // Hack to expose react to other modules
    registry.add('module', 'react', {
        exports: React
    });
    registry.add('module', 'styled-jsx', {
        exports: styledJsx
    });

    registry.add('view', 'react', {
        render: (currentResource, renderContext, view) => {
            const reactRenderType = renderContext.getRequest().getAttribute('reactRenderType');
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

            if (reactRenderType && reactRenderType === 'deferred') {
                // React render, htmlRoot is not supported for this render type
                return `
                    <div id=${id} data-reactrender="${getEncodedData(props)}"></div>
                    <jahia:resource type="javascript" path="/modules/${view.bundle.getSymbolicName()}/javascript/remote.js" key="" insert="true"/>
                    <jahia:resource type="javascript" path="/modules/npm-modules-engine/javascript/apps/reactAppShell.js" key=""/>
                `;
            }

            // SSR
            const element = React.createElement(view.component, {...props, currentResource, renderContext});

            const client = getClient(renderContext);
            registry.get('module', 'helpers').exports.apollo = client;

            const styles = {};
            // Render function that also get styles from styled-jsx
            let renderFunction = element => {
                const res = ReactDOMServer.renderToString(element);
                styles.resolved = flushToHTML();
                return res;
            };

            return getMarkupFromTree({tree: element, renderFunction: renderFunction}).then(r => {
                const initialState = client.extract();
                const stylesResource = styles.resolved ? `<jahia:resource type="inline" key="styles${id}">${styles.resolved}</jahia:resource>` : '';

                if (view.htmlRoot) {
                    // rehydration is not supported for HTML root component.
                    return `
                    <html>
                      ${stylesResource}
                      ${r}
                    </html>
                    `;
                } else {
                    return `
                            <div id=${id} data-reacthydrate="${getEncodedData(props)}" data-apollostate="${getEncodedData(initialState)}">${r}</div>
                            ${stylesResource}
                            <jahia:resource type="javascript" path="/modules/${view.bundle.getSymbolicName()}/javascript/remote.js" key="" insert="true"/>
                            <jahia:resource type="javascript" path="/modules/npm-modules-engine/javascript/apps/reactAppShell.js" key=""/>
                        `;
                }
            });
        }
    });
};
