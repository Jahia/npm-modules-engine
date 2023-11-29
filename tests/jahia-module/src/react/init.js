// import {registry} from '@jahia/server-helpers';
// import TestJAreas from './npmExample/testJAreas/TestJAreas';
// import PageSimple from './jnt/page/PageSimple';
// import TestJRender from './npmExample/testJRender/TestJRender';
// import TestJRenderParameters from './npmExample/testJRender/TestJRender.parameters';
// import TestJRenderSub from './npmExample/testJRender/TestJRender.sub';
// import TestJRenderTagged from './npmExample/testJRender/TestJRender.tagged';
// import TestCurrentContent from './npmExample/testCurrentContent/TestCurrentContent';
// import PageEvent from './jnt/page/PageEvent';
// import TestJConfig from './npmExample/testJConfig/TestJConfig';
// import TestJUrl from './npmExample/testJUrl/TestJUrl';
// import NavMenu from './npmExample/navMenu/NavMenu';
// import PageNavMenu from './jnt/page/PageNavMenu';

// export function initReact() {
//     const reactView = registry.get('view', 'react');

//     registry.add('view', 'page_simple_react', reactView, {
//         target: 'jnt:page',
//         remote: 'npm',
//         component: PageSimple,
//         templateName: 'simpleReact',
//         templateType: 'html',
//         displayName: 'Simple page (react)',
//         properties: {
//             template: 'true'
//         }
//     });
//     registry.add('view', 'page_event_react', reactView, {
//         target: 'jnt:page',
//         remote: 'npm',
//         component: PageEvent,
//         templateName: 'eventsReact',
//         templateType: 'html',
//         displayName: 'Events page (react)',
//         properties: {
//             template: 'true',
//             'cache.requestParameters': 'N-*'
//         }
//     });
//     registry.add('view', 'page_navMenu_react', reactView, {
//         target: 'jnt:page',
//         remote: 'npm',
//         component: PageNavMenu,
//         templateName: 'navMenuReact',
//         templateType: 'html',
//         displayName: 'Nav Menu (react)',
//         properties: {
//             template: 'true'
//         }
//     });

//     registry.add('view', 'navMenu_react', reactView, {
//         target: 'npmExample:navMenu',
//         component: NavMenu,
//         templateName: 'react',
//         templateType: 'html',
//         displayName: 'navMenu (react)',
//         properties: {
//             'cache.mainResource': 'true'
//         }
//     });

//     registry.add('view', 'testJUrl_react', reactView, {
//         target: 'npmExample:testJUrl',
//         component: TestJUrl,
//         templateName: 'react',
//         templateType: 'html',
//         displayName: 'test jUrl (react)'
//     });

//     registry.add('view', 'testConfig_react', reactView, {
//         target: 'npmExample:testJConfig',
//         component: TestJConfig,
//         templateName: 'react',
//         templateType: 'html',
//         displayName: 'test jConfig (react)'
//     });

//     registry.add('view', 'testCurrentContent_react', reactView, {
//         target: 'npmExample:testCurrentContent',
//         component: TestCurrentContent,
//         templateName: 'react',
//         templateType: 'html',
//         displayName: 'test currentContent (react)'
//     });

//     registry.add('view', 'testJAreas_react', reactView, {
//         target: 'npmExample:testJAreas',
//         component: TestJAreas,
//         templateName: 'react',
//         templateType: 'html',
//         displayName: 'test Areas (react)'
//     });
//     registry.add('view', 'testJRender_react', reactView, {
//         target: 'npmExample:testJRender',
//         component: TestJRender,
//         templateName: 'react',
//         templateType: 'html',
//         displayName: 'test Render (react)'
//     });
//     registry.add('view', 'testJRender_parametersReact', reactView, {
//         target: 'npmExample:testJRender',
//         component: TestJRenderParameters,
//         templateName: 'parametersReact',
//         templateType: 'html',
//         displayName: 'test Render (parameters react)'
//     });
//     registry.add('view', 'testJRender_subReact', reactView, {
//         target: 'npmExample:testJRender',
//         component: TestJRenderSub,
//         templateName: 'subReact',
//         templateType: 'html',
//         displayName: 'test Render (sub react)'
//     });
//     registry.add('view', 'testJRender_taggedReact', reactView, {
//         target: 'npmExample:testJRender',
//         component: TestJRenderTagged,
//         templateName: 'taggedReact',
//         templateType: 'html',
//         displayName: 'test Render (tagged react)'
//     });
// }
