import {getNode, getResourceArgs, setResult} from './util';

export default function () {
    let {resource, options} = getResourceArgs(arguments);
    var node = getNode(resource, options.data.root.currentResource.getNode());
    var childIterator = node.getNodes();
    var result = [];
    while (childIterator.hasNext()) {
        result.push(childIterator.nextNode());
    }

    return setResult(result, this, options);
}
