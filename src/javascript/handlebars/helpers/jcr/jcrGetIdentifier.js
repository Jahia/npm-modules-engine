import {getNode, setResult} from "./util";

export default function (resource, options) {
    const node = getNode(resource, options.data.root.currentResource.getNode());

    var result = node.getIdentifier();
    return setResult(result, this, options);
}