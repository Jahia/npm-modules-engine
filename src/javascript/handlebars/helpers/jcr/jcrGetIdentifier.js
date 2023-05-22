import {getNode, setResult} from './util';

export default function (options) {
    const node = getNode(options.hash, options.data.root.currentResource.getNode());

    var result = node.getIdentifier();
    return setResult(result, this, options);
}
