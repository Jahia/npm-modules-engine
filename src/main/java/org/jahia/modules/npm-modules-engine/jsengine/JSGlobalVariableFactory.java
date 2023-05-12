package org.jahia.modules.npm-modules-engine.jsengine;

public interface JSGlobalVariableFactory {
    String getName();

    Object getObject(ContextProvider context);
}
