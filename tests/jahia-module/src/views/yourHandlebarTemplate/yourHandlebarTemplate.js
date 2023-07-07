import {registry} from '@jahia/server-helpers';

export default {
    render: (currentResource, renderContext) => {
        const hbs = registry.get('module', 'handlebars');
        console.log('handlebars = ', hbs);
        const Handlebars = hbs.exports;
        const templateStr = currentResource.getNode().getProperty('value').getString();
        const template = Handlebars.compile(templateStr);
        return template({currentResource, renderContext});
    }
}
