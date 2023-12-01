import {registry} from '@jahia/server-helpers';
import renderFilterTest from './renderfilter/renderFilterTest';
import yourHandlebarTemplate from './views/yourHandlebarTemplate/yourHandlebarTemplate';
import {menuEntryCss} from './helpers/menuEntryCss';
import {registerJahiaComponents} from '@jahia/server-jsx';
import * as reactComponents from './react';

registerJahiaComponents(reactComponents);


registry.add('view', 'yourHandlebarTemplate_default', yourHandlebarTemplate, {
    target: 'jnt:yourHandlebarTemplate',
    templateType: 'html',
    displayName: 'Default template'
});

registry.add('render-filter', 'test', renderFilterTest, {
    target: 'render:50',
    applyOnNodeTypes: 'jnt:bigText'
});

const Handlebars = registry.get('module', 'handlebars');
Handlebars.exports.registerHelper('menuEntryCss', menuEntryCss);
Handlebars.exports.registerHelper('toJson', obj => JSON.stringify(obj));
Handlebars.exports.registerHelper('debug', obj => {
    console.log(obj);
    /* eslint-disable-next-line no-debugger */
    debugger;
});
Handlebars.exports.registerHelper('param', (param, options) => {
    const value = options.data.root.renderContext.getRequest().getParameter(param);
    return value;
});
