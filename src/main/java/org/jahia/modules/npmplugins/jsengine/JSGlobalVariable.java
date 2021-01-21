package org.jahia.modules.npmplugins.jsengine;

public interface JSGlobalVariable {

    public String getName();

    public Object getInstance(ContextProvider context);
}
