/*
 * Copyright (C) 2002-2023 Jahia Solutions Group SA. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jahia.modules.npm.modules.engine.views;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;

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
