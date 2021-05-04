import {SafeString} from 'handlebars';
import {getNode} from "./util";

function convertValue(jcrValue) {
    if (jcrValue.getType() === 10) {
        return jcrValue.getNode().getPath();
    }

    return new SafeString(jcrValue.getString());
}

export default function (resource, name, options) {
    const node = getNode(resource, options.data.root.currentResource.getNode());

    if (!node.hasProperty(name)) {
        return '';
    }

    const property = node.getProperty(name);
    if (property.isMultiple()) {
        return property.getValues().map(convertValue)
    } else {
        return convertValue(property.getValue());
    }
}
