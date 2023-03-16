## Views

JS-based views are resolved with the help of `JSScriptResolver` class. 

In npm-plugins views are JS functions, which takes "resource" and "render context" parameters (as java objects), and returns a string. 
In this context, "view" and "template" terms are equivalent. 
Resource contains the node, the optional view name to use, and some other rendering parameters. 
Render context is the global context for the request. 

### Views as templates

In npm-pluins, templates are only views - there's no need to create a template in the studio anymore. Page templates will be created as views for the `jnt:page` type - the `jnt:template` type is not used at all.
These views need to declare `template: 'true'` in their properties. The `SimpleTemplateNodeFilter` will detect that and change the behaviour of the standard `TemplateNodeFilter` when this property is detected.

### View resolution

When it needs to render a node, the render service asks for registered "ScriptResolver" implementation - NPM plugins provide an implementation [`JSScriptResolver.java`](./JSScriptResolver.java) which is looking into the registry to get these view.
The implementation is currently simple and not really optimized, but it basically looks into the registry for any view that matches the criteria. [`JSView`](./JSView.java) contains the view metadata, and [`JSScript`](./JSScript.java) the rendering part. 
It simply dispatches the resource/context to the JS function, and return the generated result.

A complex JS view example can be found [here](https://github.com/Jahia/npm-plugin-example/blob/8e111d7303a81a48dd064e1c75a1a31797e4d126/src/views/test/test.js) - it has been removed since as all views have been replaced with handlebars, but can still be interesting for review. Using pure JS view is not something people will do for basic views, but are the base for implementing any js templating language (as handlebars).

### Registering views

Views can be registered in the registry during module initialization, as objects with the type `view` and the following data : 

- `target` : The node type for this view
- `templateType` : The template type (html)
- `templateName` : The template name 
- `displayName` : The display name (for choice lists)
- `properties` : The view properties (same as the one in the `.properties` file for JSP views)
- `render` : The render function, which will take the `Resource`, `RenderContext`, and the view object itself as parameters.

For example :

```javascript
import {registry} from '@jahia/server-helpers';

registry.add("view", "page-simple", {
  target: 'jnt:page',
  templateName: 'simple',
  templateType: 'html',
  displayName: "Simple template",
  properties: {
    template: 'true'
  },
  render: (resource,context) => 'toto'
});
```

### Views autodetection

If the module contains a "views" folder at the root level, we will try to detect views here, as in standard modules with JSP.

The file extension need to be recognized by a `ViewParser` which will create the view object (actually a `JSFileView`).

The target, templateType and templateName can be derived from the file name and path. 
The properties are read from a `.properties` file with the same name.
The render function itself can be implemented by the `ViewParser` or taken from a base view.

### Render function

Render function must be implemented by all view. They can either return a string, or a promise which will resolve to a string.

Most template-based views will extend a base view, which implement the render function.
Render function will load the template from a "template" or a "templateFile" property.

For example, the base "handlebars" view reads the template from a file :

```
        render: (currentResource, renderContext, view) => {
            const templateStr = osgi.loadResource(view.bundle, view.templateFile);
            ...
        }
```

### Handlebars view


A base view for handlebars is provided [here](../../../../../../../javascript/README.md#handlebars) and can be used to register handlebars views : 

A [`ViewParser`](./ViewParser.java) is also provided ([`HandlebarsParser`](./hbs/HandlebarsParser.java)), so that you don't need to manually register views - just putting a `.hbs` file in `/views/type` folder and the view will be automatically detected and registered.

Full documentation for handlebars templates [here](./handlebars.md).
