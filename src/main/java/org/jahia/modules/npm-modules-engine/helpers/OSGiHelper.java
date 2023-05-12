package org.jahia.modules.npm-modules-engine.helpers;


import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npm-modules-engine.jsengine.ContextProvider;
import org.jahia.modules.npm-modules-engine.jsengine.GraalVMEngine;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.osgi.framework.Bundle;

import javax.inject.Inject;

public class OSGiHelper {

    private ContextProvider contextProvider;

    private JahiaTemplateManagerService templateManagerService;

    public OSGiHelper(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public Bundle getBundle(String symbolicName) {
        JahiaTemplatesPackage packageById = templateManagerService.getTemplatePackageById(symbolicName);
        if (packageById != null) {
            return packageById.getBundle();
        }

        return null;
    }

    public String loadResource(Bundle bundle, String path) {
        return GraalVMEngine.loadResource(bundle, path);
    }

    @Inject
    @OSGiService
    public void setTemplateManagerService(JahiaTemplateManagerService templateManagerService) {
        this.templateManagerService = templateManagerService;
    }
}
