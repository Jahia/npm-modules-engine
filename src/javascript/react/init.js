import {registry} from '@jahia/server-helpers';
import React from "react";
import ReactDOMServer from "react-dom/server";
import {flushToHTML} from 'styled-jsx/server'
import styledJsx from 'styled-jsx/style'

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

            const uniqueId = 'reactTarget' + Math.floor(Math.random() * 100000000);
            const data = {
                view: view.key,
                bundleKey: view.bundle.getSymbolicName(),
                id: uniqueId,
                props:{ currentResource: {}, renderContext: {}, view }
            };
            let encodedData = JSON.stringify(data).replace(/[\u00A0-\u9999<>\&";]/g, i => '&#'+i.charCodeAt(0)+';');

            if (reactRenderType && reactRenderType === 'deferred') {
                // React render
                return `
                    <div id=${uniqueId} data-reactrender="${encodedData}"></div>
                    <jahia:resource type="javascript" path="/modules/${view.bundle.getSymbolicName()}/javascript/remote.js" key="" insert="true"/>
                    <jahia:resource type="javascript" path="/modules/npm-plugins/javascript/apps/reactAppShell.js" key=""/>
                `
            } else {
                // SSR
                const element = React.createElement(view.component, {currentResource, renderContext, view});
                const res = ReactDOMServer.renderToString(element)
                const styles = flushToHTML()
                let stylesResource = styles ? `<jahia:resource type="inline" key="styles${uniqueId}">${styles}</jahia:resource>` : '';

                return `
                    <div id=${uniqueId} data-reacthydrate="${encodedData}">${res}</div>
                    ${stylesResource}
                    <jahia:resource type="javascript" path="/modules/${view.bundle.getSymbolicName()}/javascript/remote.js" key="" insert="true"/>
                    <jahia:resource type="javascript" path="/modules/npm-plugins/javascript/apps/reactAppShell.js" key=""/>
                `
            }
        }
    });
}