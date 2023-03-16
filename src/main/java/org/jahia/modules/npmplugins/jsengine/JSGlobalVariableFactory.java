package org.jahia.modules.npmplugins.jsengine;

public interface JSGlobalVariableFactory {
    String getName();

    Object getObject(ContextProvider context);
}
