import {getNode, setResult} from './util';

export default function (resource, options) {
    const node = getNode(resource, options.data.root.currentResource.getNode());

    var result = node.getName();
    return setResult(result, this, options);
}
