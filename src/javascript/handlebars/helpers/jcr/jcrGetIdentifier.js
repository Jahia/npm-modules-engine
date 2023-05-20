import {getNode, getResourceArgs, setResult} from './util';

export default function () {
    let {resource, options} = getResourceArgs(arguments);
    const node = getNode(resource, options.data.root.currentResource.getNode());

    var result = node.getIdentifier();
    return setResult(result, this, options);
}
