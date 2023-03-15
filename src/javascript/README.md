## Javascript context initialization

This script is executed first on every JS context.

### I18N setup

This allows the use of i18next in JS context. The backend will directly read translations from `locales/<locale>.json` inside the current bundle.

### Handlebars

A base view for handling handlebars templates is registered : 

```
registry.add('view', 'handlebars', {
```

This view require the `templateFile` property to be set to the path of the template file (inside the current bundle).

A set of generics ( array, collection, comparison, html, match, math, misc, number, object, path, regex, string, url ) and custom helpers ( gql, jcr, render, util ) are registered.
The gql handlebars helper, which relies on the GraalVM GraphQL Helper to execute gql queries internally, makes it possible to retrieve all possible data in jahia :

```
{{#gql}}
    {
        jcr {
            nodeByPath(path:"{{jcrGetPath currentResource}}") {
                properties {
                    name
                    value
                }
            }
        }
    }
{{/gql}}
```

The Handlebars module itself is also available in the registry, if you need to directly access it : 

```
    registry.add('module', 'handlebars', {
```


### React setup
