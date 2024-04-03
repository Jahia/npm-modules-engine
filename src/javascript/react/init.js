import {server} from '@jahia/js-server-core-private';
import React from 'react';
import ReactDOMServer from 'react-dom/server';
import {createStyleRegistry, StyleRegistry} from 'styled-jsx';
import {ServerContextProvider} from '@jahia/js-server-core';
import i18n from 'i18next';
import {I18nextProvider} from 'react-i18next';

export default () => {
    server.registry.add('view', 'react', {
        viewRenderer: 'react'
    });

    server.registry.add('viewRenderer', 'react', {
        render: (currentResource, renderContext, view) => {
            const bundleKey = view.bundle.getSymbolicName();
            // I18next
            const locale = renderContext.getRequest().getAttribute('org.jahia.utils.i18n.forceLocale') || currentResource.getLocale();
            // Load locales
            i18n.loadNamespaces(bundleKey);
            i18n.loadLanguages(locale.getLanguage());
            // Set module namespace and current language
            i18n.setDefaultNamespace(bundleKey);
            i18n.changeLanguage(locale.getLanguage());

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
                bundle: bundleKey
            };

            // SSR
            const styleRegistry = createStyleRegistry();
            const currentNode = currentResource.getNode();
            const mainNode = renderContext.getMainResource().getNode();
            const element =
                React.createElement(StyleRegistry, {registry: styleRegistry}, React.createElement(ServerContextProvider, {renderContext, currentResource, currentNode, mainNode, bundleKey}, React.createElement(I18nextProvider, {i18n}, React.createElement(view.component, {...props}))));

            // Some server side components are using dangerouslySetInnerHTML to render their content,
            // we need to clean the output to avoid having unwanted divs in the final output (e.g. <unwanteddiv>content</unwanteddiv>)
            const cleanedRenderedElement = ReactDOMServer.renderToString(element)
                .replace(/<unwanteddiv>/g, '')
                .replace(/<\/unwanteddiv>/g, '');

            const styles = ReactDOMServer.renderToStaticMarkup(styleRegistry.styles());
            const stylesResource = styles ? `<jahia:resource type="inline" key="styles${id}">${styles}</jahia:resource>` : '';
            if (currentResource.getContextConfiguration() === 'page') {
                return `<html>${cleanedRenderedElement}${stylesResource}</html>`;
            }

            return `${cleanedRenderedElement}${stylesResource}`;
        }
    });
};
