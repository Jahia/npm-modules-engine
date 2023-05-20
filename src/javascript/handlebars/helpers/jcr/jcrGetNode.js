import {getNode, getResourceArgs, setResult} from './util';

export default function () {
    let {resource, options} = getResourceArgs(arguments);
    var result = getNode(resource, options.data.root.currentResource.getNode());
    return setResult(result, this, options);
}
