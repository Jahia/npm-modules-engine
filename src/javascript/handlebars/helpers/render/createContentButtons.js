import {SafeString} from "handlebars";
import {render} from '@jahia/server-helpers';

export default function (options) {
    const {nodeTypes, childName, editCheck} = options.hash;
    return new SafeString(render.createContentButtons(childName || '*', nodeTypes || [], !!editCheck, options.data.root.renderContext));
}