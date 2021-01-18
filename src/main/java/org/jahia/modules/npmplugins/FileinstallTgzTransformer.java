package org.jahia.modules.npmplugins;

import org.osgi.service.component.annotations.Component;
import org.apache.felix.fileinstall.ArtifactListener;
import org.apache.felix.fileinstall.ArtifactUrlTransformer;

import java.io.File;
import java.net.URL;

/**
 * Artifact transformer for tgz files dropped in file install
 */
@Component(service={ArtifactUrlTransformer.class, ArtifactListener.class}, property = {"url.handler.protocol=npm"}, immediate = true)
public class FileinstallTgzTransformer implements ArtifactUrlTransformer {
    @Override
    public boolean canHandle(File file) {
        return file.getName().endsWith(".tgz");
    }

    @Override
    public URL transform(URL url) throws Exception {
        return new URL("npm://"+url.toExternalForm());
    }
}