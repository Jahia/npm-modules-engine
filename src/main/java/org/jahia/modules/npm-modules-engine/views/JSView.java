package org.jahia.modules.npm.modules.engine.views;

import org.graalvm.polyglot.Value;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.render.View;
import org.osgi.framework.Bundle;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class JSView implements View, Comparable<View> {
    protected String registryKey;
    protected String key;
    protected JahiaTemplatesPackage module;
    protected Properties properties;
    protected Properties defaultProperties;
    protected String path;
    protected String displayName;
    protected String target;
    protected String templateType;

    public JSView(String registryKey, String viewName, JahiaTemplatesPackage module, String target, String templateType) {
        this.registryKey = registryKey;
        this.key = viewName;
        this.module = module;
        this.target = target;
        this.templateType = templateType;
    }

    public JSView(Map<String, Object> jsValue) {
        registryKey = jsValue.get("key").toString();
        key = jsValue.get("templateName") != null ? jsValue.get("templateName").toString() : "default";

        Bundle bundle = ((Value)jsValue.get("bundle")).asHostObject();
        module = ServicesRegistry.getInstance().getJahiaTemplateManagerService().getTemplatePackageById(bundle.getSymbolicName());

        target = jsValue.get("target").toString();
        templateType = jsValue.get("templateType").toString();

        displayName = jsValue.containsKey("displayName") ? jsValue.get("displayName").toString() : getKey();

        properties = new Properties();
        if (jsValue.containsKey("properties")) {
            properties.putAll((Map<?, ?>) jsValue.get("properties"));
        }
        defaultProperties = new Properties();
        path = getModule().getBundleKey() + "/" + getRegistryKey();
    }

    public Map<String,Object> getValue(ContextProvider contextProvider) {
        return contextProvider.getRegistry().get("view", getRegistryKey());
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
        return path;
    }

    @Override
    public String getInfo() {
        return getPath();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getDefaultProperties() {
        return defaultProperties;
    }

    public void setDefaultProperties(Properties defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public String getTarget() {
        return target;
    }

    public String getTemplateType() {
        return templateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JSView)) return false;
        JSView jsView = (JSView) o;
        return registryKey.equals(jsView.registryKey) && key.equals(jsView.key) && module.equals(jsView.module) && Objects.equals(path, jsView.path) && target.equals(jsView.target) && templateType.equals(jsView.templateType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryKey, key, module, path, target, templateType);
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
