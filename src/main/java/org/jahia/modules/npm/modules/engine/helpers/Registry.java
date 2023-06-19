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
package org.jahia.modules.npm.modules.engine.helpers;

import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;

import java.util.*;
import java.util.stream.Collectors;

import static org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine.JS;

public class Registry {
    private final ContextProvider context;
    Map<String, Map<String, Object>> registryMap = new HashMap<>();

    public Registry(ContextProvider context) {
        this.context = context;
    }

    public Map<String, Object> get(String type, String key) {
        return registryMap.get(type + "-" + key);
    }

    public List<Map<String, Object>> find(Map<String, Object> filter) {
        Collection<Map<String, Object>> result = registryMap.values();

        return result.stream()
                .filter(item -> filter.entrySet().stream().allMatch(f -> f.getValue().equals(item.get(f.getKey()))))
                .collect(Collectors.toList());
    }

    public void add(String type, String key, Map<String, Object>... arguments) {
        if (registryMap.containsKey(type + "-" + key)) {
            throw new IllegalArgumentException("Entry for " + type + " / " + key + " already exist");
        }
        addOrReplace(type, key, arguments);
    }

    public void addOrReplace(String type, String key, Map<String, Object>... arguments) {
        Map<String, Object> object = composeServices(arguments);

        object.put("key", key);
        object.put("type", type);
        object.put("bundle", context.getContext().getBindings(JS).getMember("bundle"));
        registryMap.put(type + "-" + key, object);
    }

    public void remove(String type, String key) {
        registryMap.remove(type + "-" + key);
    }

    public Map<String, Object> composeServices(Map<String, Object>... arguments) {
        return Arrays.stream(arguments).reduce(new HashMap<>(), (result, element) -> {
            Map<String, Object> m = new HashMap<>(result);
            m.putAll(element);
            return m;
        });
    }
}
