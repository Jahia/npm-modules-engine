import {SafeString} from 'handlebars';
import {getNode, setResult} from './util';

const JCR_WEAKREFERENCE_PROPERTY_TYPE = 10;

export default function () {
    function convertValue(jcrValue) {
        if (options.hash.renderer) {
            // eslint-disable-next-line no-undef
            const choiceListRendererService = Java.type('org.jahia.services.content.nodetypes.renderer.ChoiceListRendererService').getInstance();
            const renderer = choiceListRendererService.getRenderers().get(options.hash.renderer);
            if (renderer) {
                return renderer.getObjectRendering(options.data.root.renderContext, jcrValue.getDefinition(), jcrValue.getString());
            }
        }

        if (jcrValue.getType() === JCR_WEAKREFERENCE_PROPERTY_TYPE) {
            return jcrValue.getNode().getUrl();
        }

        return new SafeString(jcrValue.getString());
    }

    var options = arguments[arguments.length - 1];
    var resource = options.data.root.currentResource;
    var name;
    if (arguments.length === 2) {
        name = arguments[0];
    } else {
        resource = arguments[0];
        name = arguments[1];
    }

    const node = getNode(resource, options.data.root.currentResource.getNode());

    if (!node.hasProperty(name)) {
        return '';
    }

    const property = node.getProperty(name);

    var result;
    if (property.isMultiple()) {
        result = property.getValues().map(convertValue);
    } else {
        result = convertValue(property.getValue());
    }

    return setResult(result, this, options);
}
