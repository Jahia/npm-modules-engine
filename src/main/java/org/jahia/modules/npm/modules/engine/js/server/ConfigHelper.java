package org.jahia.modules.npm.modules.engine.js.server;

import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.taglibs.functions.Functions;

import java.util.HashMap;

/**
 * Java helper to expose OSGi configuration values to Handlebar helpers
 */
public class ConfigHelper {
    private final ContextProvider context;

    public ConfigHelper(ContextProvider context) {
        this.context = context;
    }

    public ProxyArray getConfigPids() {
        return ProxyArray.fromArray(Functions.getConfigPids().toArray());
    }

    public ProxyArray getConfigFactoryIdentifiers(String factoryPid) {
        return ProxyArray.fromArray(Functions.getConfigFactoryIdentifiers(factoryPid).toArray());
    }

    public ProxyObject getConfigValues(String configPid) {
        return ProxyObject.fromMap(new HashMap<>(Functions.getConfigValues(configPid)));
    }

    public ProxyObject getConfigFactoryValues(String factoryPid, String factoryIdentifier) {
        return ProxyObject.fromMap(new HashMap<>(Functions.getConfigFactoryValues(factoryPid, factoryIdentifier)));
    }

    public String getConfigValue(String configPid, String key) {
        return Functions.getConfigValue(configPid, key);
    }

    public String getConfigFactoryValue(String factoryPid, String factoryIdentifier, String key) {
        return Functions.getConfigFactoryValue(factoryPid, factoryIdentifier, key);
    }
}
