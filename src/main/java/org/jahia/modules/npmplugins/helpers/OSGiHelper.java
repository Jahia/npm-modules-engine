package org.jahia.modules.npmplugins.helpers;


import org.apache.commons.io.IOUtils;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRTemplate;
import org.osgi.framework.Bundle;
import pl.touk.throwing.ThrowingSupplier;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Optional;

public class OSGiHelper {

    private ContextProvider contextProvider;

    public OSGiHelper(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public String loadResource(Bundle bundle, String path) {
        return GraalVMEngine.loadResource(bundle, path);
    }
}
