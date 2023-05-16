package org.jahia.modules.npm.modules.engine.npmhandler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerService;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Npm protocol handler
 */
@Component(service={URLStreamHandlerService.class}, property = {"url.handler.protocol=npm"}, immediate = true)
public class NpmProtocolStreamHandler extends AbstractURLStreamHandlerService {

    @Override
    public URLConnection openConnection(URL u) throws IOException {
        return new NpmProtocolConnection(u);
    }
}