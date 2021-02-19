package org.jahia.modules.npmplugins.helpers;


import org.apache.commons.io.IOUtils;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.JSGlobalVariable;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRTemplate;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import pl.touk.throwing.ThrowingSupplier;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Optional;

@Component(service = JSGlobalVariable.class, immediate = true)
public class OSGiHelper implements JSGlobalVariable {
    @Override
    public String getName() {
        return "osgi";
    }

    @Override
    public Object getInstance(ContextProvider contextProvider) {
        return new Instance(contextProvider);
    }

    private static String getSourceFile(Bundle bundle, String path) throws RepositoryException {
        return JCRTemplate.getInstance().doExecuteWithSystemSession(session -> {
            JahiaTemplatesPackage pkg = BundleUtils.getModule(bundle);
            String sourcePath = "/modules/" + pkg.getIdWithVersion() + "/sources";
            if (!session.itemExists(sourcePath)) {
                return null;
            }
            JCRNodeWrapper sources = session.getNode(sourcePath);
            if (sources.hasNode(path)) {
                try {
                    return IOUtils.toString(sources.getNode(path).getFileContent().downloadFile());
                } catch (IOException e) {
                    throw new RepositoryException(e);
                }
            }
            return null;
        });
    }

    public static String loadResource(Bundle bundle, String path) {
        try {
            return Optional
                    .ofNullable(getSourceFile(bundle, path))
                    .orElseGet(ThrowingSupplier.unchecked(() -> IOUtils.toString(bundle.getResource(path))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class Instance {
        private ContextProvider contextProvider;

        public Instance(ContextProvider contextProvider) {
            this.contextProvider = contextProvider;
        }

        public String loadResource(Bundle bundle, String path) {
            return OSGiHelper.loadResource(bundle, path);
        }
    }
}
