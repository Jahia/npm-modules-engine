import {SafeString} from "handlebars";

export default function(resource, name) {
    const node = resource.getClass().getName() === 'org.jahia.services.render.Resource' ? resource.getNode() : resource;

    if (!node.hasProperty(name)) {
        return "";
    }

    return new SafeString(node.getProperty(name).getString());
};
