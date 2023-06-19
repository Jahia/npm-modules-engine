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
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.osgi.framework.Bundle;

import javax.inject.Inject;

public class OSGiHelper {

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

    public String loadResource(Bundle bundle, String path) {
        return GraalVMEngine.loadResource(bundle, path);
    }
}
