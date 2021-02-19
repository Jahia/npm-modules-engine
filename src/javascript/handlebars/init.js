import {registry, osgi} from '@jahia/server-helpers';
import * as helpers from './helpers';
import Handlebars from "handlebars";


export default () => {
    Object.keys(helpers).forEach(k => {
        Handlebars.registerHelper(k, helpers[k]);
    });

    // Hack to expose handlebars to other modules
    registry.add("module", "handlebars", {
        exports: Handlebars
    });

    registry.add("view", "handlebars", {
        render: (currentResource, renderContext, view) => {
            const templateStr = osgi.loadResource(view.bundle, view.templateFile)
            const template = Handlebars.compile(templateStr);
            return template({currentResource, renderContext});
        }
    });

}