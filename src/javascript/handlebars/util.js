export function getNode(hashParameters, session) {
    if (hashParameters.identifier) {
        return session.getNodeByIdentifier(hashParameters.identifier);
    }

    if (hashParameters.path) {
        return session.getNode(hashParameters.path);
    }

    return null;
}

export function setResult(result, context, options) {
    if (options.hash.varName) {
        context[options.hash.varName] = result;
    } else {
        return result;
    }
}
