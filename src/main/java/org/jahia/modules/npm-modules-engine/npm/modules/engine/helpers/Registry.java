package org.jahia.modules.npm.modules.engine.helpers;

import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;

import java.util.*;
import java.util.stream.Collectors;

import static org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine.JS;

public class Registry {
    private ContextProvider context;
    Map<String, Map<String, Object>> registryMap = new HashMap<>();

    public Registry(ContextProvider context) {
        this.context = context;
    }

    public Map<String, Object> get(String type, String key) {
        return registryMap.get(type + "-" + key);
    }

    public List<Map<String, Object>> find(Map<String, Object> filter) {
        Collection<Map<String, Object>> result = registryMap.values();

        if (filter.containsKey("target")) {
            /*
             *             result = result
             *                 .filter(item => {
             *                     return item.targets && item.targets
             *                         .map(t => t.id)
             *                         .includes(filters.target);
             *                 })
             *                 .sort((a, b) => {
             *                     const foundA = a.targets && a.targets.find(t => t.id === filters.target);
             *                     const foundB = b.targets && b.targets.find(t => t.id === filters.target);
             *                     const priorityA = foundA && Number(foundA.priority);
             *                     const priorityB = foundB && Number(foundB.priority);
             *
             *                     if (isNaN(priorityA) && isNaN(priorityB)) {
             *                         return 0;
             *                     }
             *
             *                     if (isNaN(priorityA)) {
             *                         return -1;
             *                     }
             *
             *                     if (isNaN(priorityB)) {
             *                         return 1;
             *                     }
             *
             *                     return priorityA - priorityB;
             *                 });
             */
        }

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
        if (object.get("targets") != null) {
            //
        }

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
