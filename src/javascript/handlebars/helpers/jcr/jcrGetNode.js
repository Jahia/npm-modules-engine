import {getNode, setResult} from "./util";

export default function (resource, options) {
    var result = getNode(resource, options.data.root.currentResource.getNode());
    return setResult(result, this, options);
}
