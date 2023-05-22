import {getNode, setResult} from './util';

export default function (options) {
    var result = getNode(options.hash, options.data.root.currentResource.getNode());
    return setResult(result, this, options);
}
