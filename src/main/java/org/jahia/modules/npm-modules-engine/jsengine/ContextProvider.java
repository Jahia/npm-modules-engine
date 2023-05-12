package org.jahia.modules.npm.modules.engine.jsengine;

import org.graalvm.polyglot.Context;
import org.jahia.modules.npm.modules.engine.helpers.Registry;
import org.jahia.modules.npm.modules.engine.helpers.RegistryHelper;
import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ContextProvider {

    private boolean isActive = true;
    private int version;
    private final Context context;

    private Collection<Bundle> registeredBundles = new ArrayList<>();
    private Map<String, Object> helpers = new HashMap<>();

    public ContextProvider(Context cx, int version) {
        this.context = cx;
        this.version = version;
    }

    public Context getContext() {
        return context;
    }

    public int getVersion() {
        return version;
    }

    public Collection<Bundle> getRegisteredBundles() {
        return registeredBundles;
    }

    public Map<String, Object> getHelpers() {
        return helpers;
    }

    public Registry getRegistry() {
        return ((RegistryHelper) getHelpers().get("registry")).getRegistry();
    }

    public void close() {
        context.close();
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

}
