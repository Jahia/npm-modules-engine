import {server} from '@jahia/js-server-engine-private';

const backend = {
    type: 'backend',

    init: function () {
    },

    read: function (language, namespace, callback) {
        const bundle = server.osgi.getBundle(namespace);
        if (bundle) {
            let content = server.osgi.loadResource(bundle, 'locales/' + language + '.json', true) ||
                server.osgi.loadResource(bundle, 'javascript/locales/' + language + '.json', true);
            if (content) {
                callback(null, JSON.parse(content));
            } else {
                callback('cannot load translation file');
            }
        } else {
            callback('cannot find bundle ' + namespace);
        }
    },

    create: function () {
    }
};

export default backend;
