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
package org.jahia.modules.npm.modules.engine.js.server;

import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.Registry;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistryHelper {
    private final ContextProvider context;

    public RegistryHelper(ContextProvider context) {
        this.context = context;
    }

    public Object get(String type, String key) {
        return ProxyObject.fromMap(context.getRegistry().get(type, key));
    }

    public List<Object> find(Map<String, Object> filter) {
        return context.getRegistry().find(filter).stream().map(ProxyObject::fromMap).collect(Collectors.toList());
    }

    public List<Object> find(Map<String, Object> filter, String orderBy) {
        return context.getRegistry().find(filter, orderBy).stream().map(ProxyObject::fromMap).collect(Collectors.toList());
    }

    public void add(String type, String key, Map<String, Object>... arguments) {
        context.getRegistry().add(type, key, arguments);
    }

    public void addOrReplace(String type, String key, Map<String, Object>... arguments) {
        context.getRegistry().addOrReplace(type, key, arguments);
    }

    public void remove(String type, String key) {
        context.getRegistry().remove(type, key);
    }

    public Registry getRegistry() {
        return context.getRegistry();
    }
}
