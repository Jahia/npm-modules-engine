/*
 * Copyright (C) 2002-2023 Jahia Solutions Group SA. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jahia.modules.npm.modules.engine.helpers;


import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npm.modules.engine.helpers.injector.OSGiService;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.services.render.RenderException;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class OSGiHelper {
    private static final Logger logger = LoggerFactory.getLogger(OSGiHelper.class);

    private final ContextProvider contextProvider;

    private JahiaTemplateManagerService templateManagerService;

    public OSGiHelper(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Inject
    @OSGiService
    public void setTemplateManagerService(JahiaTemplateManagerService templateManagerService) {
        this.templateManagerService = templateManagerService;
    }

    public Bundle getBundle(String symbolicName) {
        JahiaTemplatesPackage packageById = templateManagerService.getTemplatePackageById(symbolicName);
        if (packageById != null) {
            return packageById.getBundle();
        }
        return null;
    }

    public String loadResource(Bundle bundle, String path, boolean optional) throws RenderException {
        String result = GraalVMEngine.loadResource(bundle, path);
        if (!optional && result == null) {
            // todo (BACKLOG-21263) handle exception correctly in NPM views
            //throw new RenderException(String.format("Unable to load resource: %s from bundle: %s", path, bundle.getSymbolicName()));
            logger.error("Unable to load resource: {} from bundle: {}", path, bundle.getSymbolicName());
        }
        return result;
    }
}
