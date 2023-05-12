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

// SetTimeout polyfill
global.setTimeout = ((cb, t) => {
    if (t === 0) {
        // eslint-disable-next-line no-new
        new Promise(resolve => {
            console.log('Execute timeout');
            cb();
            resolve();
        });
    }
});
