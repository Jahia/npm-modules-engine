## Views

JS-based views are resolved with the help of `JSScriptResolver` class. 


### Registering views

Views can be registered in the registry as objects with the type `view` and the following data : 

- `target` : The node type for this view
- `templateType` : The template type (html)
- `templateName` : The template name 
- `displayName` : The display name (for choice lists)
- `properties` : The view properties (same as the one in the `.properties` file for JSP views)
- `render` : The render function, which will take the `Resource`, `RenderContext`, and the view object itself as parameters.

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

A `ViewParser` is also provided, so that you don't need to manually register views - just putting a `.hbs` file in `/views/type` folder and the view will be automatically detected and registered.

