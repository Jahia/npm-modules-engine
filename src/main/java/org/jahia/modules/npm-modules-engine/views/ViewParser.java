package org.jahia.modules.npm-modules-engine.views;

import org.jahia.modules.npm-modules-engine.views.JSView;
import org.osgi.framework.Bundle;

public interface ViewParser {
    boolean canHandle(String viewPath);

    JSView parseView(Bundle bundle, String viewPath);
}
