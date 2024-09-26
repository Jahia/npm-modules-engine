package org.jahia.modules.npm.modules.engine.js.server;

import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrHelper {

    private static final Logger logger = LoggerFactory.getLogger(JcrHelper.class);

    public Object doExecuteAsGuest(JCRCallback<Object> callback) {
        JCRTemplate template = JCRTemplate.getInstance();
        Object result = null;
        try {
            String workspace = "live";
            result = template.doExecute(JahiaUserManagerService.GUEST_USERNAME, null, workspace, null, callback);
        } catch (Exception e) {
            logger.error("Error while executing callback as guest", e);
        }
        return result;
    }
}
