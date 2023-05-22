export function getNode(hashParameters, currentNode) {
    if (hashParameters.identifier) {
        return session.getNodeByIdentifier(identifier);
    }
    if (hashParameters.path) {
        return session.getNode(resource);
    }
    if (hashParameters.relPath) {
        return currentNode.getNode(resource);
    }
    if (hashParameters.resource) {
        return resource.getNode();
    }
    if (hashParameters.node) {
        return hashParameters.node;
    }
    return currentNode;
}

export function setResult(result, context, options) {
    if (options.hash.varName) {
        context[options.hash.varName] = result;
    } else {
        return result;
    }
}
