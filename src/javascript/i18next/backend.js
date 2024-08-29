import {server} from '@jahia/js-server-core-private';

const backend = {
    type: 'backend',

    init: function () {
    },

    read: function (language, namespace, callback) {
        const bundle = server.osgi.getBundle(namespace);
        if (bundle) {
            let content = server.osgi.loadResource(bundle, 'locales/' + language + '.json', true);
            if (content) {
                callback(null, JSON.parse(content));
            } else {
                // No locales found
                callback(null, {});
            }
        } else {
            callback('cannot find bundle: ' + namespace);
        }
    },

    create: function () {
    }
};

export default backend;
