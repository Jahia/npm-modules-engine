import {registry} from '@jahia/server-helpers';
import React from "react";
import ReactDOMServer from "react-dom/server";

export default () => {
    // Hack to expose react to other modules
    registry.add('module', 'react', {
        exports: React
    });

    registry.add('view', 'react', {
        render: (currentResource, renderContext, view) => {
            const reactRenderType = currentResource.getModuleParams().get('reactRenderType');
            if (reactRenderType) {

            } else {
                const element = React.createElement(view.component, {currentResource, renderContext, view});
                const res = ReactDOMServer.renderToString(element)
                const uniqueId = 'reactTarget' + Math.floor(Math.random() * 100000000);
                const data = {
                    view: view.key,
                    bundleKey: view.bundle.getSymbolicName(),
                    id: uniqueId,
                    props:{ currentResource: {}, renderContext: {}, view }
                };

                let encodedData = JSON.stringify(data).replace(/[\u00A0-\u9999<>\&";]/g, i => '&#'+i.charCodeAt(0)+';');

                return `
                    <div id=${uniqueId} data-reactcomponent="${encodedData}">${res}</div>
                    <jahia:resource type="javascript" path="/modules/${view.bundle.getSymbolicName()}/javascript/remote.js" key="" insert="true"/>
                    <jahia:resource type="javascript" path="/modules/npm-plugins/javascript/apps/reactAppShell.js" key=""/>
                `
            }
        }
    });
}