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
package org.jahia.modules.npm.modules.engine.views.hbs;

import org.apache.commons.lang.StringUtils;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npm.modules.engine.views.JSFileView;
import org.jahia.modules.npm.modules.engine.views.JSView;
import org.jahia.modules.npm.modules.engine.views.ViewParser;
import org.jahia.registries.ServicesRegistry;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class HandlebarsParser implements ViewParser {
    private static final Logger logger = LoggerFactory.getLogger(HandlebarsParser.class);

    @Override
    public boolean canHandle(String viewPath) {
        return viewPath.endsWith(".hbs");
    }

    @Override
    public JSView parseView(Bundle bundle, String viewPath) {
        if (viewPath.endsWith(".hbs")) {
            JahiaTemplatesPackage module = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getTemplatePackageById(bundle.getSymbolicName());
            if (module == null) {
                logger.warn("Unable to register view '{}', because module '{}' not found", viewPath, bundle.getSymbolicName());
                return null;
            }

            String[] parts = StringUtils.split(viewPath, "/");
            String templateType = isHTMLTemplateType(parts) ? "html" : parts[3];
            String pageName = StringUtils.substringAfterLast(viewPath, "/");
            String[] viewNameParts = StringUtils.split(pageName, ".");

            String nodeType = parts[1] + ":" + viewNameParts[0];
            JSFileView fileView = new JSFileView("handlebars", viewPath, viewNameParts.length > 2 ? viewNameParts[1] : "default", module, nodeType, templateType);
            fileView.setProperties(new Properties());

            URL props = bundle.getResource(StringUtils.substringBeforeLast(viewPath, ".hbs") + ".properties");
            if (props != null) {
                try (InputStream inStream = props.openStream()) {
                    fileView.getProperties().load(inStream);
                } catch (IOException e) {
                    logger.error("Cannot read", e);
                }
            }

            fileView.setDefaultProperties(new Properties());
            return fileView;
        }
        return null;
    }

    /**
     * If the parts length is equal to 5, the template is located under /components/<namespace>/<component>
     * which is the folder for the HTML template type
     *
     * @param parts of the path
     * @return true if the template is in the folder dedicated to the HTML template type
     */
    private boolean isHTMLTemplateType(String[] parts) {
        return parts.length == 4;
    }
}
