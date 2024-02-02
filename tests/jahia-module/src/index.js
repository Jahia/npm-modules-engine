import {server} from '@jahia/js-server-engine';
import renderFilterTest from './renderfilter/renderFilterTest';
import yourHandlebarTemplate from './views/yourHandlebarTemplate/yourHandlebarTemplate';
import {menuEntryCss} from './helpers/menuEntryCss';
import {registerJahiaComponents} from '@jahia/js-server-engine';
import * as reactComponents from './react';
import Handlebars from 'handlebars';

registerJahiaComponents(reactComponents);

server.registry.add('view', 'yourHandlebarTemplate_default', yourHandlebarTemplate, {
    nodeType: 'jnt:yourHandlebarTemplate',
    templateType: 'html',
    name: 'Default template',
    componentType: 'template'

});

// TODO: to be moved to a dedicated server.registry.registerRenderFilter to provide typings
server.registry.add('service', 'test-renderFilter', {
    serviceType: 'renderFilter',
    prepare: (renderContext, resource, chain) => {
        return null;
    },
    execute: (previousOut, renderContext, resource, chain) => {
        return previousOut.replace('toto', 'tutu');
    },
    priority: '5.5',
    applyOnNodeTypes: 'jnt:bigText'
});

// TODO: to be moved to a dedicated server.registry.registerAction to provide typings, also provide typings for the return object of doExecute
server.registry.add('service', 'test-action', {
    serviceType: 'action',
    doExecute: (httpServletRequest, renderContext, resource, jcrSessionWrapper, map, urlResolver) => {
        return {
            resultCode: 200,
            json: {
                resourcePath: resource.getNode().getPath(),
                blabla: 'blabla'
            }
        };
    },
    name: 'jsAction',
    requireAuthenticatedUser: false,
    requiredMethods: 'GET'
});

// TODO: handle properties in the values
// TODO: to be moved to a dedicated server.registry.registerChoiceListInitializer to provide typings
server.registry.add('service', 'jsChoiceListInitializerTest', {
    serviceType: 'choiceListInitializer',
    getChoiceListValues: (extendedPropertyDefinition, param, values, locale, context) => {
        return [
            {
                displayName: 'Test 1',
                value: 'test1'
            },
            {
                displayName: 'Test 2',
                value: 'test2'
            }
        ];
    }
});

// TODO: to be moved to a dedicated server.registry.registerCacheKeyPartGenerator to provide typings
server.registry.add('service', 'jsCacheKeyPartGenerator', {
    serviceType: 'cacheKeyPartGenerator',
    getValue: (resource, renderContext, properties) => {
        return 'test';
    },
    replacePlaceholders: (renderContext, value) => {
        return value + '_replaced'
    }
});

// TODO: to be moved to a dedicated server.registry.registerRuleGlobalObject to provide typings
server.registry.add('service', 'jsModuleGlobalObject', {
    serviceType: 'moduleGlobalObject',
    object: {
        hello: (node) => {
            console.log('hello: ' + node.getPath());
        }
    }
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
