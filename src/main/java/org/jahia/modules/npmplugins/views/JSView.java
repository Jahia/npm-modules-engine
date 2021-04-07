package org.jahia.modules.npmplugins.views;

import org.graalvm.polyglot.Value;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.render.View;
import org.osgi.framework.Bundle;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class JSView implements View, Comparable<View> {
    private final String registryKey;
    private final String key;
    private final JahiaTemplatesPackage module;
    private final Properties properties;
    private final Properties defaultProperties;
    private String displayName;

    public JSView(Map<String, Object> jsValue) {
        registryKey = jsValue.get("key").toString();
        key = jsValue.get("templateName") != null ? jsValue.get("templateName").toString() : "default";
        displayName = jsValue.containsKey("displayName") ? jsValue.get("displayName").toString() : getKey();

        Bundle bundle = ((Value)jsValue.get("bundle")).asHostObject();
        module = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getTemplatePackageById(bundle.getSymbolicName());

        properties = new Properties();
        if (jsValue.containsKey("properties")) {
            properties.putAll((Map<?, ?>) jsValue.get("properties"));
        }
        defaultProperties = new Properties();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public JahiaTemplatesPackage getModule() {
        return module;
    }

    @Override
    public String getModuleVersion() {
        return module != null && module.getVersion() != null ? module.getVersion().toString() : null;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getFileExtension() {
        return "js";
    }

    @Override
    public String getPath() {
        return getModule().getBundleKey() + "/" + getRegistryKey();
    }

    @Override
    public String getInfo() {
        return getPath();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public Properties getDefaultProperties() {
        return defaultProperties;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    @Override
    public int compareTo(View otherView) {
        if (module == null) {
            if (otherView.getModule() != null) {
                return 1;
            } else {
                return key.compareTo(otherView.getKey());
            }
        } else {
            if (otherView.getModule() == null) {
                return -1;
            } else if (!module.equals(otherView.getModule())) {
                return module.getName().compareTo(otherView.getModule().getName());
            } else {
                return key.compareTo(otherView.getKey());
            }
        }
    }

}
