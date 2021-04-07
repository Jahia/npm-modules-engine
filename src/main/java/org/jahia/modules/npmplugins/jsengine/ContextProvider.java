package org.jahia.modules.npmplugins.jsengine;

import org.graalvm.polyglot.Context;
import org.jahia.modules.npmplugins.helpers.Registry;
import org.jahia.modules.npmplugins.helpers.RegistryHelper;
import org.osgi.framework.Bundle;

import java.util.*;

public class ContextProvider {

    private boolean isActive = true;
    private final Context context;

    private Collection<Bundle> registeredBundles = new ArrayList<>();
    private Map<String, Object> helpers = new HashMap<>();

    public ContextProvider(Context cx) {
        this.context = cx;
    }

    public Context getContext() {
        return context;
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
