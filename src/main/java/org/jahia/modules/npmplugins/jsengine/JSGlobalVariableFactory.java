package org.jahia.modules.npmplugins.jsengine;

import java.util.Map;

public interface JSGlobalVariableFactory {
    Map<String,Object> getHelperInstances(ContextProvider context);
}
