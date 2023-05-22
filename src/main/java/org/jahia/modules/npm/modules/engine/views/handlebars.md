# Handlebars templating

Handlebars support is done directly in npm-modules-engine modules, by registering an extendable base view 'handlebars' which implements the render method and automatic registration of .hbs files.

## Adding views

Handlebars views are automatically detected if they are placed in a "views" folder, with the same naming convention as in a standard Jahia module :

`views/<node_type>/<template_type>/<node_type>.<view_name>.hbs`

View properties can be provided with a `.properties` file.

### Manual registration

It's usually not needed to manually register handlebars views in the registry, as you can use the naming convention to let the npm-modules-engine module handle it.
However, if you don't use this convention, you will have to declare your own view using the registry, extending the predefined 'handlebars' view :

```javascript
registry.add("view", "page-simple", registry.get('view', 'handlebars'), {
  target: 'jnt:test',
  templateName: 'sub',
  templateType: 'html',
  displayName: "Sub template",
  properties: {
  },
  templateFile: "src/test/test.sub.hbs"
});
```

The implementation needs the templateFile - the path of the file within the bundle. It uses
the [OSGi helper](./src/main/java/org/jahia/modules/npm/engine/helpers/OSGiHelper.java) to read the file from the
bundle. Other properties are "standard" view properties (not handlebars specific), used by the `JSScriptResolver` .

The [handlebars init code](./src/javascript/handlebars/init.js) initialize the handlebars engine, register all available
helpers (currently a non-extendable list of modules), exposes the base view and the engine itself if one wants to use it
directly ( like in the "yourHandlebarsTemplate.js" example )

## Handlebars helpers

NPM modules comes with a set of handlebars helpers, providing the different functionalities that are required when builing a Jahia template.

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

**Named params**

Object values

**Example**

```handlebars
{{obj v1="a" v2="b"}}
<!-- results in: {v1: "a", v2: "b"} -->
```

#### [{{date}}](./src/javascript/handlebars/helpers/util/date.js)

Formats a date, based on dayjs

**Params**

- `date` : date input
- `format` : date format

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

All JCR helpers takes a "node" as first parameters. Node can be defined in different ways :

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

- `node` : node to use

**Named params**

- `varName` : Name of the variable where the result will be stored instead of being returned

**Example**

```handlebars
{{jcrGetIdentifer "/sites/digitall"}}
<!-- results in: '095fb1d8-b42a-4f5c-9794-35b6b46a2a96' -->
```

#### [{{jcrGetName}}](./src/javascript/handlebars/helpers/jcr/jcrGetName.js)

Get the name of a node

**Params**

- `node` : node to use

**Named params**

- `varName` : Name of the variable where the result will be stored instead of being returned

**Example**

```handlebars
{{jcrGetName "/sites/digitall"}}
<!-- results in: 'digitall' -->
```

#### [{{jcrGetNode}}](./src/javascript/handlebars/helpers/jcr/jcrGetNode.js)

Get the node object (`javax.jcr.Node`), mainly used with `varName` or nested in another expression.

**Params**

- `node` : node to use

**Named params**

- `varName` : Name of the variable where the result will be stored instead of being returned

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

- `node` : node to use

**Named params**

- `varName` : Name of the variable where the result will be stored instead of being returned

**Example**

```handlebars
{{jcrGetPath "095fb1d8-b42a-4f5c-9794-35b6b46a2a96"}}
<!-- results in: '/sites/digitall' -->
```

#### [{{jcrGetProperty}](./src/javascript/handlebars/helpers/jcr/jcrGetProperty.js)

Get a node property value (as string). Can optionally take a `ChoiceListRenderer`.

If property is multiple, returns an array of values.

**Params**

- `node` : node to use
- `propertyName` :  name of the property to get

**Named params**

- `varName` : Name of the variable where the result will be stored instead of being returned
- `render` : Name of the ChoiceListRenderer to use

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


**Content block**

The GQL query

**Params**

N/A

**Named params**

- `varName` : Name of the variable where the result will be stored instead of being returned
- `variables` : Object containing all variables to bind to the query 
- `operationName` : Graphql operation name

**Example**

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

Add cache dependency to the specified node

**Params**

N/A

**Named params**

- `path` : node path to be added as a dependency
- `uuid` : node uuid to be added as a dependency
- `flushOnPathMatchingRegexp` : Regexp matching path that should flush this component

**Example**

```handlebars
{{addCacheDependency path="/sites/digitall/page"}}
```

#### [{{addResources}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)

Add a static resource link to the page (CSS or JS)

**Params**

N/A

**Named params**

- `type` : Type of the resources (will define where to find (in css direcory for css, in javascript directory for javascript). If not set the content of the tag body will be used as the asset itself and the resources attribute can be left empty.
- `resources` : The list of resources they will be included in the defined order. If the tag body is not empty, it will be used as the asset itself and the resources attribute can be left empty.
- `async` : async attribute to load resource asynchronously
- `defer` : defer attribute to load resource asynchronously
- `targetTag` : The target tag where the link will be inserted
- `rel` : For css resource type, this attribute specify the relationship type of the link
- `media` : For css resource type, this attribute specifies the media (or medias, comma separated) on which the css will be applied.
- `condition` : optional html browser condition (ex : if gte IE 6)
- `insert` : If set to true the resource is inserted at the top of the resources list. Otherwise it is appended to the end. [false]
- `title` : An optional title value for this asset (in case of a link).
- `key` : key for managing uniqueness of inline type. 
- `var` : Name of the exported pageContext variable to hold the value specified in the action.

**Example**

```handlebars
{{addResources type="css" resources="styles.css"}}
```

#### [{{renderModule}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)

Renders a node using the render chain, with the specified view/template . 

**Params**

N/A

**Named params**

- `path` : Relative (to the current node) or absolute path to the node to include
- `nodeName` : Name of the variable that exposes the node in the page context
- `view` : The view used to display the node. Overrides the view defined on the node itself (layout tab in edit mode), which is used when this attribute is not set.
- `templateType` : The output type to use. By default, inherited from parent fragment, or HTML.
- `editable` : Enables or disables edition of this content in edit mode. Mainly used for absolute or references.
- `nodeTypes` : Space separated list of allowed node types. If the node does not match any node type, it won't be displayed. Edit mode will forbid drop of any incompatible node.
- `listLimit`
- `constraints`
- `var` : Name of the exported pageContext variable to hold the value specified in the action.
- `checkConstraints`
- `showAreaButton`
- `skipAggregation` : Enables or disables aggregation for sub fragments.

**Example**

```handlebars
Display an existing node by absolute path : {{renderModule path="/path" view="view"}}
Display a subnode, with constraint on node type : {{renderModule path="simpletext" nodeTypes="jnt:text"}}
Creates a placeholder for creating subnodes : {{renderModule path="*" nodeTypes="jnt:test"}}
```

#### [{{renderInclude}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)

Include another view of the same node, using render chain.

**Params**

N/A

**Named params**

- `view` : The view used to display the node. Overrides the view defined on the node itself (layout tab in edit mode), which is used when this attribute is not set.
- `templateType` : The output type to use. By default, inherited from parent fragment, or HTML.
- `var` : Name of the exported pageContext variable to hold the value specified in the action.

**Example**

```handlebars
{{renderInclude view="view"}}
```

#### [{{renderComponent}}](./src/javascript/handlebars/helpers/render/jcrGetPath.js)

Renders the view of a component based on a nodetype and properties values.

Not to be confused with renderModule, which renders an existing node - this one takes a nodetype name and properties values, and renders the associated view, without having an existing node. This will actually create a temporary node and renders it.

**Params**

N/A

**Named params**

- `primaryNodeType` : The component nodetype
- `properties` : object holding the properties values
- `view` : The view used to display the node. Overrides the view defined on the node itself (layout tab in edit mode), which is used when this attribute is not set.
- `templateType` : The output type to use. By default, inherited from parent fragment, or HTML. (defaults to 'html')
- `contextConfiguration` : context configuration for render chain (defaults to 'module')
- `name` : name of the temporary node, if needed (defaults to 'temp-node')
- `path` : path of the temporary node, if needed (defaults to '/')

**Example**

```handlebars
Renders an area : {{renderComponent name="area-test" primaryNodeType="jnt:absoluteArea"}}
Renders a navigation menu : {{renderComponent name="navMenu" primaryNodeType="jnt:navMenu" properties=(obj j:maxDepth="2" j:baselineNode="home" j:menuItemView="menuElement")}}
```