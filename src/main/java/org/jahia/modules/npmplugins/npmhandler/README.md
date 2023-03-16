## NPM plugins format handler

This package provide classes to parse and handle NPM-like modules (i.e. tgz files created with `npm pack`)

### PAX-URL protocol

The [NpmProtocolStreamHandler](./NpmProtocolStreamHandler.java) provides a new `npm` protocol : `npm://` . It's mainly a wrapper around any other mvn protocol, so npm modules can be installed with URLs like `npm://file://xxx.tgz` or `npm://http://xxx/xx/tgz`.

It will transform the wrapped resources into a usable OSGi bundle. NPM plugins URLs must always be wrapped with the `npm` protocol.

### Fileinstall

In order for fileinstall to be able to install NPM plugins, we need to tell it to use the `npm://` protocol around the `.tgz` files. This is done inside [FileinstallTgzTransformer](./FileinstallTgzTransformer.java)

