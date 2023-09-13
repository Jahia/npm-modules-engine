import {SafeString} from "handlebars";
import {render} from '@jahia/server-helpers';

const allowedRenderAttributes = ['content', 'node', 'path', 'editable', 'advanceRenderingConfig', 'templateType', 'view'];

export default function (options) {
    // filter out available options hash
    const attrs = Object.keys(options.hash).reduce((acc, attr) => {
        if (allowedRenderAttributes.includes(attr)) {
            acc[attr] = options.hash[attr]
        } else {
            console.warn(`${attr} is not a valid attribute for render helper, attribute ignored`)
        }
        return acc;
    }, {})

    if (attrs.length === 0) {
        console.warn(`no valid attribute found within ${options.hash}`)
        return ''
    }
    const output = new SafeString(render.render(attrs, options.data.root.renderContext))

    // Special handling for var
    if (options.hash.var) {
        this[options.hash.var] = output
        return ''
    }

    return output
}