import {registry} from '@jahia/server-helpers';
import renderFilterTest from './renderfilter/renderFilterTest';
import yourHandlebarTemplate from './views/yourHandlebarTemplate/yourHandlebarTemplate';
import {menuEntryCss} from './helpers/menuEntryCss';


registry.add('view', 'yourHandlebarTemplate_default', yourHandlebarTemplate, {
    target: 'jnt:yourHandlebarTemplate',
    templateType: 'html',
    displayName: 'Default template'
});

registry.add('render-filter', 'test', renderFilterTest, {
    target: 'render:50',
    applyOnNodeTypes: 'jnt:bigText'
});

import TestReact from '../components/npmExample/testReact/testReact';
const reactView = registry.get('view', 'react');
registry.add('view', 'reactComponent_default', reactView, {
    target: 'npmExample:testReact',
    component: TestReact,
    templateName: 'default',
    templateType: 'html',
    displayName: 'testReact'
});

const Handlebars = registry.get('module', 'handlebars');
Handlebars.exports.registerHelper('menuEntryCss', menuEntryCss);
Handlebars.exports.registerHelper('toJson', obj => JSON.stringify(obj));
Handlebars.exports.registerHelper('debug', obj => {
    console.log(obj);
    debugger;
});
Handlebars.exports.registerHelper('param', (param, options) => {
    const value = options.data.root.renderContext.getRequest().getParameter(param);
    return value;
});
