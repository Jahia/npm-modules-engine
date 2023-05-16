import i18next from 'i18next';
import backend from './backend';

export default () => {
    i18next
        .use(backend)
        .init({
            fallbackLng: 'en',
            ns: 'npm-modules-engine',
            defaultNS: 'npm-modules-engine',
            initImmediate: false
        });
};
