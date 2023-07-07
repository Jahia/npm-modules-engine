import {SafeString} from 'handlebars';
import {getNode, setResult} from './util';

const JCR_WEAKREFERENCE_PROPERTY_TYPE = 10;

export default function (name, options) {
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

        // In case result is stored in a variable we don't safe it, otherwise we do to prevent direct display of it
        return options.hash.varName ? jcrValue.getString() : new SafeString(jcrValue.getString());
    }

    const node = getNode(options.hash, options.data.root.currentResource.getNode());

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
