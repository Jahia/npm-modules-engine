import {getNode} from "./util";

export default function (resource, options) {
    const node = getNode(resource, options.data.root.currentResource.getNode());

    return node.getIdentifier();
}
