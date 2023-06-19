## GraphQL extension

This package provides required extension for JS template rendering. 

They could actually be moved to the graphql core module.

### Get rendered component

The field `npm/renderedComponent` returns the HTML rendering for a specific node definition - node type, properties and eventually sub node definitions. 
The result is the same as if a node was created and render requested, but no node are actually created.
