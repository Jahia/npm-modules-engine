import Handlebars from 'handlebars';

export default {
    render: (currentResource, renderContext) => {
        const templateStr = currentResource.getNode().getProperty('value').getString();
        const template = Handlebars.compile(templateStr);
        return template({currentResource, renderContext});
    }
};
