import initI18next from './i18next/init';
import initHandlebars from './handlebars/init';
import initReact from './react/init';
import {registry} from '@jahia/server-helpers';

registry.add('module', 'helpers', {
    exports: {}
});

initI18next();
initHandlebars();
initReact();
