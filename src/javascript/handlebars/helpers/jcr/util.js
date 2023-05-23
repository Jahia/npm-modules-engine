export function getNode(hashParameters, currentNode) {
    if (hashParameters.identifier) {
        let session = currentNode.getSession();
        return session.getNodeByIdentifier(hashParameters.identifier);
    }

    if (hashParameters.path) {
        let session = currentNode.getSession();
        return session.getNode(hashParameters.path);
    }

    if (hashParameters.relPath) {
        return currentNode.getNode(hashParameters.relPath);
    }

    if (hashParameters.resource) {
        return hashParameters.resource.getNode();
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
