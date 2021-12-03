package org.jahia.modules.npmplugins.views;

import org.jahia.modules.npmplugins.views.JSView;
import org.osgi.framework.Bundle;

public interface ViewParser {
    boolean canHandle(String viewPath);

    JSView parseView(Bundle bundle, String viewPath);
}
