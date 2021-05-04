const uuidRegex = /[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/;

export function getNode(resource, currentNode) {
    if (typeof resource === 'string') {
        let session = currentNode.getSession();
        if (uuidRegex.test(resource)) {
            return session.getNodeByIdentifier(resource)
        } else if (resource.startsWith("/")) {
            return session.getNode(resource)
        } else {
            return currentNode.getNode(resource);
        }
    } else if (resource.getClass().getName() === 'org.jahia.services.render.Resource') {
        return resource.getNode();
    }

    return resource;
}
