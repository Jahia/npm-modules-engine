import {getNode, setResult} from './util';

export default function (options) {
    const node = getNode(options.hash, options.data.root.currentResource.getNode());
    const childIterator = node.getNodes();
    const result = [];
    while (childIterator.hasNext()) {
        result.push(childIterator.nextNode());
    }

    return setResult(result, this, options);
}
