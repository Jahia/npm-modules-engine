import {registerJahiaComponents, server} from '@jahia/js-server-core';
import renderFilterTest from './renderfilter/renderFilterTest';
import yourHandlebarTemplate from './views/yourHandlebarTemplate/yourHandlebarTemplate';
import {menuEntryCss} from './helpers/menuEntryCss';
import * as reactComponents from './react/server';
import Handlebars from 'handlebars';

registerJahiaComponents(reactComponents);

server.registry.add('view', 'yourHandlebarTemplate_default', yourHandlebarTemplate, {
    nodeType: 'jnt:yourHandlebarTemplate',
    templateType: 'html',
    name: 'Default template',
    componentType: 'template'
});

server.registry.add('render-filter', 'test', renderFilterTest, {
    target: 'render:50',
    applyOnNodeTypes: 'jnt:bigText'
});

Handlebars.registerHelper('menuEntryCss', menuEntryCss);
Handlebars.registerHelper('toJson', obj => JSON.stringify(obj));
Handlebars.registerHelper('debug', obj => {
    console.log(obj);
    /* eslint-disable-next-line no-debugger */
    debugger;
});
Handlebars.registerHelper('param', (param, options) => {
    const value = options.data.root.renderContext.getRequest().getParameter(param);
    return value;
});

Handlebars.registerHelper('inlineScriptTest', () => {
    return '<script type="text/javascript">\n' +
        '            console.log(\'Executing inline script...\');\n' +
        '            document.addEventListener(\'DOMContentLoaded\', function () {\n' +
        '                var newDiv = document.createElement(\'div\');\n' +
        '                newDiv.id = \'testInlineScript\';\n' +
        '                document.body.appendChild(newDiv);\n' +
        '            });\n' +
        '        </script>';
});
