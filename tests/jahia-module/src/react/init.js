import {registry} from '@jahia/server-helpers';
import TestAreas from './npmExample/testAreas/TestAreas';
import PageSimple from './jnt/page/PageSimple';
import TestRender from './npmExample/testRender/TestRender';
import TestRenderParameters from './npmExample/testRender/TestRender.parameters';
import TestRenderSub from './npmExample/testRender/TestRender.sub';
import TestRenderTagged from './npmExample/testRender/TestRender.tagged';
import TestCurrentContent from './npmExample/testCurrentContent/TestCurrentContent';
import PageEvent from './jnt/page/PageEvent';
import TestConfig from './npmExample/testConfig/testConfig';

export function initReact() {
    const reactView = registry.get('view', 'react');

    registry.add('view', 'page_simple_react', reactView, {
        target: 'jnt:page',
        remote: 'npm',
        component: PageSimple,
        templateName: 'simpleReact',
        templateType: 'html',
        displayName: 'Simple page (react)',
        properties: {
            template: 'true'
        }
    });
    registry.add('view', 'page_event_react', reactView, {
        target: 'jnt:page',
        remote: 'npm',
        component: PageEvent,
        templateName: 'eventsReact',
        templateType: 'html',
        displayName: 'Events page (react)',
        properties: {
            template: 'true',
            'cache.requestParameters': 'N-*'
        }
    });

    registry.add('view', 'testConfig_react', reactView, {
        target: 'npmExample:testConfig',
        component: TestConfig,
        templateName: 'react',
        templateType: 'html',
        displayName: 'test jConfig (react)'
    });

    registry.add('view', 'testCurrentContent_react', reactView, {
        target: 'npmExample:testCurrentContent',
        component: TestCurrentContent,
        templateName: 'react',
        templateType: 'html',
        displayName: 'test currentContent (react)'
    });

    registry.add('view', 'testAreas_react', reactView, {
        target: 'npmExample:testAreas',
        component: TestAreas,
        templateName: 'react',
        templateType: 'html',
        displayName: 'test Areas (react)'
    });
    registry.add('view', 'testRender_react', reactView, {
        target: 'npmExample:testRender',
        component: TestRender,
        templateName: 'react',
        templateType: 'html',
        displayName: 'test Render (react)'
    });
    registry.add('view', 'testRender_parametersReact', reactView, {
        target: 'npmExample:testRender',
        component: TestRenderParameters,
        templateName: 'parametersReact',
        templateType: 'html',
        displayName: 'test Render (parameters react)'
    });
    registry.add('view', 'testRender_subReact', reactView, {
        target: 'npmExample:testRender',
        component: TestRenderSub,
        templateName: 'subReact',
        templateType: 'html',
        displayName: 'test Render (sub react)'
    });
    registry.add('view', 'testRender_taggedReact', reactView, {
        target: 'npmExample:testRender',
        component: TestRenderTagged,
        templateName: 'taggedReact',
        templateType: 'html',
        displayName: 'test Render (tagged react)'
    });
}
