

export default () => {
    import {server} from "@jahia/js-server-core";

    server.registry.add('viewRenderer', 'myCustomViewRenderer', {
        render: (currentResource, renderContext, view) => {
            return view.render(currentResource, renderContext);
        }
    });

    server.registry.add('view', 'my_custom_view', {
        nodeType: 'jnt:bigText',
        templateType: 'html',
        name: 'customViewRenderer',
        componentType: 'view',
        viewRenderer: 'myCustomViewRenderer',
        render: (currentResource) => {
            return `Hello World ${currentResource.getNode().getPath()}`;
        }
    });
}