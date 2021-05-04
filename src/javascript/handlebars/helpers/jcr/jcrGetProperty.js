import {SafeString} from 'handlebars';

export default function (resource, name) {
    const node = resource.getClass().getName() === 'org.jahia.services.render.Resource' ? resource.getNode() : resource;

    if (!node.hasProperty(name)) {
        return '';
    }

    if (node.getProperty(name).getType() === 10) {
        return node.getProperty(name).getNode().getPath();
    }

    return new SafeString(node.getProperty(name).getString());
}
