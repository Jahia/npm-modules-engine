import {getNode, setResult} from './util';

export default function (options) {
    var node = getNode(options.hash, options.data.root.currentResource.getNode());
    var childIterator = node.getNodes();
    var result = [];
    while (childIterator.hasNext()) {
        result.push(childIterator.nextNode());
    }

    return setResult(result, this, options);
}
