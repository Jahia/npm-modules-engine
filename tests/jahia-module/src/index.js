import {server} from '@jahia/js-server-engine';
import renderFilterTest from './renderfilter/renderFilterTest';
import yourHandlebarTemplate from './views/yourHandlebarTemplate/yourHandlebarTemplate';
import {menuEntryCss} from './helpers/menuEntryCss';
import {registerJahiaComponents} from '@jahia/js-server-engine';
import * as reactComponents from './react';
import Handlebars from 'handlebars';

registerJahiaComponents(reactComponents);

server.registry.add('view', 'yourHandlebarTemplate_default', yourHandlebarTemplate, {
    target: 'jnt:yourHandlebarTemplate',
    templateType: 'html',
    displayName: 'Default template'
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
