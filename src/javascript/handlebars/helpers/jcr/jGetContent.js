import {getNode, setResult} from '../../hbsUtils';
import {render} from '@jahia/server-helpers';

export default function (options) {
    const node = getNode(options.hash, options.data.root.currentResource.getNode().getSession());
    if (node) {
        return setResult(render.transformToJsNode(node, options.hash.includeChildren ? options.hash.includeChildren : false, options.hash.includeDescendants ? options.hash.includeDescendants : false, options.hash.includeAllTranslations ? options.hash.includeAllTranslations : false), this, options);
    }
}
