import {osgi} from '@jahia/server-helpers';

const backend = {
    type: 'backend',

    init: function(services, backendOptions, i18nextOptions) {
    },

    read: function(language, namespace, callback) {
        const bundle = osgi.getBundle(namespace)
        if (bundle) {
            let content = osgi.loadResource(bundle, 'locales/' + language +'.json') || osgi.loadResource(bundle, 'javascript/locales/' + language +'.json');
            if (content) {
                callback(null, JSON.parse(content));
            } else {
                callback("cannot load translation file");
            }
        } else {
            callback("cannot find bundle " + namespace);
        }
    },

    create: function(languages, namespace, key, fallbackValue) {
        /* save the missing translation */
    }
}

export default backend;