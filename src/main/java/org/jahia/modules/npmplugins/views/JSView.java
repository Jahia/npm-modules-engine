package org.jahia.modules.npmplugins.views;

import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.render.View;
import org.osgi.framework.Bundle;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class JSView implements View, Comparable<View> {
    private final Map<String, Object> jsValue;
    private final String key;
    private final JahiaTemplatesPackage module;
    private final Properties properties;
    private final Properties defaultProperties;

    public JSView(Map<String, Object> jsValue) {
        this.key = jsValue.get("templateName") != null ? jsValue.get("templateName").toString() : "default";

        Bundle bundle = (Bundle) jsValue.get("bundle");
        this.module = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getTemplatePackageById(bundle.getSymbolicName());

        this.jsValue = jsValue;

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
        return jsValue.containsKey("displayName") ? jsValue.get("displayName").toString() : getKey();
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
        return jsValue.get("key").toString();
    }

    public Map<String, Object> getJsValue() {
        return jsValue;
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
