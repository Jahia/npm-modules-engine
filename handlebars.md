# Handlebars templating

Handlebars support is done directly in npm-plugins modules, by registering an extendable base view 'handlebars' which implements the render method.

## Registering views

In order to use it, one have to declare its own view using the registry, extending this predefined 'handlebars' view :

```javascript
registry.add("view", "page-simple", registry.get('view', 'handlebars'), {
  target: 'jnt:test',
  templateName: 'sub',
  templateType: 'html',
  displayName: "Sub template",
  templateFile: "src/views/test/test.sub.hbs"
});
```

[Or, use the naming convention defined in BACKLOG-15773 , which exempt the user to explicitly declare its view into the registry.]

The implementation needs the templateFile - the path of the file within the bundle. It uses the [OSGi helper](./src/main/java/org/jahia/modules/npmplugins/helpers/OSGiHelper.java) to read the file from the bundle. Other properties are "standard" view properties (not handlebars specific), used by the `JSScriptResolver` .

The [handlebars init code](./src/javascript/handlebars/init.js) initialize the handlebars engine, register all available helpers (currently a non-extendable list of modules), exposes the base view and the engine itself if one wants to use it directly ( like in the "yourHandlebarsTemplate.js" example )

## Handlebars helpers

NPM plugins comes with a set of handlebars helpers, providing the different functionalities that are required when builing a Jahia template.

### Generic helper

Generic helper comes from [handlebars-helpers](https://github.com/helpers/handlebars-helpers) project.

These collection of helpers are included : 
- [array](https://github.com/helpers/handlebars-helpers#array)
- [collection](https://github.com/helpers/handlebars-helpers#collection)
- [comparison](https://github.com/helpers/handlebars-helpers#comparison)
- [html](https://github.com/helpers/handlebars-helpers#ahtml)
- [match](https://github.com/helpers/handlebars-helpers#match)
- [math](https://github.com/helpers/handlebars-helpers#math)
- [misc](https://github.com/helpers/handlebars-helpers#misc)
- [number](https://github.com/helpers/handlebars-helpers#number)
- [object](https://github.com/helpers/handlebars-helpers#object)
- [path](https://github.com/helpers/handlebars-helpers#path)
- [regex](https://github.com/helpers/handlebars-helpers#regex)
- [string](https://github.com/helpers/handlebars-helpers#string)
- [url](https://github.com/helpers/handlebars-helpers#url)

These additional helpers are available :

#### [{{obj}}](./src/javascript/handlebars/helpers/util/obj.js)

Creates a JS object based on hash options

**Params**

* `options` hash

**Example**

```handlebars
{{obj v1="a" v2="b"}}
<!-- results in: {v1: "a", v2: "b"} -->
```

#### [{{date}}](./src/javascript/handlebars/helpers/util/date.js)

Formats a date, based on dayjs

**Params**

* `date` 
* `format` 
* `options` hash

**Example**

```handlebars
{{date "2018-04-04T16:00:00.000Z" "YYYY"}}
<!-- results in: '2018' -->
```

### I18n

i18n helper for i18next is [handlebars-i18next](https://github.com/UUDigitalHumanitieslab/handlebars-i18next). 
It's configured so that namespace is the bundle name where the translations are defined, and translation files are located inside the bundle, under `locales/{lng}.json`

Default namespace is set to the calling bundle, default language is the current resource language (or `org.jahia.utils.i18n.forceLocale` if present in request).

### JCR

All JCR helpers takes a node as first parameters. Node can be defined in different ways :

- UUID (`"095fb1d8-b42a-4f5c-9794-35b6b46a2a96"`)
- Absolute node path (`"/sites/digitall"`)
- Node path, relative the current node (`"./subnode"`)
- Node object (`currentNode`)
- Node resource (`currentResource`)

Also most helpers can add `varName` in hash options. When defined, the result will be stored in a variable in the current context, instead of being displayed.

```handlebars
{{jcrGetNode "/sites/digitall" varName="siteNode"}}
```

#### [{{jcrGetIdentifer}}](./src/javascript/handlebars/helpers/jcr/jcrGetIdentifier.js)

Get the identifier of a node

**Params**

* `node` 
* `options` hash : varName

**Example**

```handlebars
{{jcrGetIdentifer "/sites/digitall"}}
<!-- results in: '095fb1d8-b42a-4f5c-9794-35b6b46a2a96' -->
```

#### [{{jcrGetName}}](./src/javascript/handlebars/helpers/jcr/jcrGetName.js)

Get the name of a node

**Params**

* `node` 
* `options` hash : varName

**Example**

```handlebars
{{jcrGetName "/sites/digitall"}}
<!-- results in: 'digitall' -->
```

#### [{{jcrGetNode}}](./src/javascript/handlebars/helpers/jcr/jcrGetNode.js)

Get the node object (`javax.jcr.Node`), mainly used with `varName` or nested in another expression.

**Params**

* `node` 
* `options` hash : varName

**Example**

```handlebars
{{jcrGetNode "/sites/digitall" varName="siteObject"}}
..
{{jcrGetName siteObject}}
<!-- results in: 'digitall' -->
```

#### [{{jcrGetPath}}](./src/javascript/handlebars/helpers/jcr/jcrGetPath.js)

Get the path of a node

**Params**

* `node` 
* `options` hash : varName

**Example**

```handlebars
{{jcrGetPath "095fb1d8-b42a-4f5c-9794-35b6b46a2a96"}}
<!-- results in: '/sites/digitall' -->
```

#### [{{jcrGetProperty}](./src/javascript/handlebars/helpers/jcr/jcrGetProperty.js)

Get a node property value (as string). Can optionally take a `ChoiceListRenderer`.

If property is multiple, returns an array of values.

**Params**

* `node` 
* `propertyName` 
* `options` hash : varName, renderer

**Example**

```handlebars
Get property by absolute path : {{jcrGetProperty (jcrGetPath currentResource) "prop1"}}
relative path: {{jcrGetProperty "." "prop1"}}
resource : {{jcrGetProperty currentResource "prop1"}}
node : {{jcrGetProperty (jcrGetNode currentResource) "prop1"}}
uuid : {{jcrGetProperty (jcrGetIdentifier currentResource) "prop1"}}
with renderer : {{jcrGetProperty "." "prop1" renderer='flagcountry' varName='flagObj'}} {{flagObj}}

Get multiple property by absolute path: {{jcrGetProperty "/sites/digitall" "j:installedModules"}}
```

### Graphql 

#### [{{gql}}](./src/javascript/handlebars/helpers/gql/gql.js)

The `{{gql}}` helper executes a graphql query and put the results in a variable (default "gql", can be changed in `varName` hash parameter).
This is a block helper, the query itself being the block content.

Variables to the query can be passed through the `variables` hash parameter, as an object

```handlebars
{{#gql varName="result"}}
    {
        jcr {
            nodeByPath(path:"{{jcrGetPath currentResource}}") {
                children {
                    nodes {
                        path
                    }
                }
            }
        }
    }
{{/gql}}
<!-- Use the result object -->
{{#each result.jcr.nodeByPath.children.nodes}}
    <li>path={{path}}</li>
{{/each}}
```

Variables to the query can be passed through the `variables` hash parameter, as an object :

```handlebars
{{#gql variables=(obj path="/sites")}}
    query($path:String!) {
        jcr {
            nodeByPath(path: $path) {
                name
            }
        }
    }
{{/gql}}
```

### Render 

Render helper are mostly similar to JSP "template" taglib.

#### [{{addCacheDependency}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)
#### [{{addResources}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)
#### [{{renderComponent}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)
#### [{{renderInclude}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)
#### [{{renderModule}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)
#### [{{renderOption}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)

