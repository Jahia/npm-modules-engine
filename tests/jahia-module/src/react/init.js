import {registry} from '@jahia/server-helpers';
import TestAreas from './TestAreas';

export function initReact() {
    const reactView = registry.get('view', 'react');

    registry.add('view', 'testAreasReact', reactView, {
        target: 'npmExample:testAreasReact',
        component: TestAreas,
        templateName: 'default',
        templateType: 'html',
        displayName: 'test Areas (react)'
    });
}
