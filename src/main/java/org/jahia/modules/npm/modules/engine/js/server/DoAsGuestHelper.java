package org.jahia.modules.npm.modules.engine.js.server;

import com.oracle.truffle.js.builtins.JSBuiltinsContainer;
import org.graalvm.polyglot.Value;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Locale;
import java.util.Map;

public class DoAsGuestHelper {

    private static final Logger logger = LoggerFactory.getLogger(DoAsGuestHelper.class);

    public Object doAsGuest(Map<String,Object> callback) {
        JCRTemplate template = JCRTemplate.getInstance();
        Object result = null;
        try {
            result = template.doExecute(JahiaUserManagerService.GUEST_USERNAME, null, null, null, new JCRCallback<Object>() {
                public Object doInJCR(JCRSessionWrapper jcrSessionWrapper) throws RepositoryException {
                    return Value.asValue(callback.get("callback")).execute(jcrSessionWrapper);
                }
            });
        } catch (Exception e) {
            logger.warn("Error while executing callback as guest", e);
        }
        return result;
    }
}
