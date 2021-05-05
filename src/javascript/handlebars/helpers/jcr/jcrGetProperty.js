import {SafeString} from 'handlebars';
import {getNode, setResult} from "./util";

export default function (resource, name, options) {
    function convertValue(jcrValue) {
        if (options.hash['renderer']) {
            const choiceListRendererService = Java.type('org.jahia.services.content.nodetypes.renderer.ChoiceListRendererService').getInstance();
            const renderer = choiceListRendererService.getRenderers().get(options.hash['renderer']);
            if (renderer) {
                return renderer.getObjectRendering(options.data.root.renderContext, jcrValue.getDefinition(), jcrValue.getString());
            }
        }

        if (jcrValue.getType() === 10) {
            return jcrValue.getNode().getPath();
        }

        return new SafeString(jcrValue.getString());
    }


    const node = getNode(resource, options.data.root.currentResource.getNode());

    if (!node.hasProperty(name)) {
        return '';
    }

    const property = node.getProperty(name);

    var result;
    if (property.isMultiple()) {
        result = property.getValues().map(convertValue)
    } else {
        result = convertValue(property.getValue());
    }

    return setResult(result, this, options);
}
