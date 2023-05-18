package org.jahia.modules.npm.modules.engine.views;

import org.osgi.framework.Bundle;

public interface ViewParser {
    boolean canHandle(String viewPath);

    JSView parseView(Bundle bundle, String viewPath);
}
