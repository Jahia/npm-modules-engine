package org.jahia.modules.npmplugins.views;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;

import java.util.HashMap;
import java.util.Map;

public class JSFileView extends JSView {
    public JSFileView(String registryKey, String path, String viewName, JahiaTemplatesPackage module, String target, String templateType) {
        super(registryKey, viewName, module, target, templateType);
        this.path = path;
    }

    @Override
    public String getDisplayName() {
        if (getProperties().containsKey("name")) {
            return (String) getProperties().get("name");
        }

        return key;
    }

    @Override
    public Map<String, Object> getValue(ContextProvider contextProvider) {
        Map<String, Object> viewValue = super.getValue(contextProvider);

        viewValue = new HashMap<>(viewValue);
        viewValue.put("templateType", getTemplateType());
        viewValue.put("templateName", getKey());
        viewValue.put("templateFile", getPath());
        viewValue.put("bundle", Value.asValue(getModule().getBundle()));
        viewValue.put("key", getRegistryKey());
        viewValue.put("properties", ProxyObject.fromMap(new HashMap<>()));
        viewValue.put("target", getTarget());

        return viewValue;
    }
}
