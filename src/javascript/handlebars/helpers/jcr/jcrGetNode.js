import {getNode} from "./util";

export default function (resource, options) {
    return getNode(resource, options.data.root.currentResource.getNode());
}
