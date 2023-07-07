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

const Handlebars = registry.get('module', 'handlebars');
Handlebars.exports.registerHelper('menuEntryCss', menuEntryCss);
