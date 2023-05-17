import {getNode, setResult} from "./util";

export default function (resource, options) {
    var node = getNode(resource, options.data.root.currentResource.getNode());
    var childIterator = node.getNodes();
    var result = [];
    while (childIterator.hasNext()) {
        var childNode = childIterator.nextNode();
        result.push({
            identifier : childNode.getIdentifier(),
            name : childNode.getName(),
            path : childNode.getPath(),
            url : childNode.getUrl()
        })
    }
    return setResult(result, this, options);
}
