package org.jahia.modules.npmplugins.views.hbs;

import org.apache.commons.lang.StringUtils;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.views.JSFileView;
import org.jahia.modules.npmplugins.views.JSView;
import org.jahia.modules.npmplugins.views.ViewParser;
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
            String[] parts = StringUtils.split(viewPath, "/");
            String[] viewNameParts = StringUtils.split(StringUtils.substringAfterLast(viewPath, "/"), ".");
            JahiaTemplatesPackage module = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getTemplatePackageById(bundle.getSymbolicName());

            //todo: bad replace from _ to :
            JSFileView fileView = new JSFileView("handlebars", viewPath, viewNameParts.length > 2 ? viewNameParts[1] : "default", module, parts[1].replace('_', ':'), parts[2]);
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

}
