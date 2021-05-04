export default function (resource) {
    const node = resource.getClass().getName() === 'org.jahia.services.render.Resource' ? resource.getNode() : resource;

    return node.getPath();
}
