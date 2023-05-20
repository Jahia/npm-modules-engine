const uuidRegex = /[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/;

export function getNode(resource, currentNode) {
    if (typeof resource === 'string') {
        let session = currentNode.getSession();
        if (uuidRegex.test(resource)) {
            return session.getNodeByIdentifier(resource);
        }

        if (resource.startsWith('/')) {
            return session.getNode(resource);
        }

        return currentNode.getNode(resource);
    }

    if (resource.getClass().getName() === 'org.jahia.services.render.Resource') {
        return resource.getNode();
    }

    return resource;
}

export function setResult(result, context, options) {
    if (options.hash.varName) {
        context[options.hash.varName] = result;
    } else {
        return result;
    }
}

export function getResourceArgs(args) {
    var options = args[args.length - 1];
    var resource = options.data.root.currentResource;
    if (args.length === 2) {
        resource = args[0];
    }

    return {resource, options};
}
