package org.jahia.modules.npmplugins.helpers;

import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.JSGlobalVariable;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component(service = {JSGlobalVariable.class, RegistryHelper.class}, immediate = true)
public class RegistryHelper implements JSGlobalVariable {

    private ThreadLocal<Bundle> currentRegisteringBundle = new ThreadLocal<>();
    private Registry registry = new Registry();

    @Override
    public String getName() {
        return "registry";
    }

    @Override
    public Object getInstance(ContextProvider context) {
        return new Instance(context);
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setCurrentRegisteringBundle(Bundle bundle) {
        this.currentRegisteringBundle.set(bundle);
    }

    public class Registry {
        Map<String, Map<String, Object>> registryMap = new HashMap<>();

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
            if (!object.containsKey("bundle")) {
                object.put("bundle", currentRegisteringBundle.get());
            }
            registryMap.put(type + "-" + key, object);
        }

        public void remove(String type, String key) {
            registryMap.remove(type + "-" + key);
        }

        public Map<String, Object> composeServices(Map<String, Object>... arguments) {
            return Arrays.stream(arguments).reduce((result, element) -> {
                Map<String, Object> m = new HashMap<>(result);
                m.putAll(element);
                return m;
            }).orElse(new HashMap<>());
        }
    }

    public class Instance {
        private ContextProvider context;

        public Instance(ContextProvider context) {
            this.context = context;
        }

        public Map<String, Object> get(String type, String key) {
            return registry.get(type, key);
        }

        public List<Map<String, Object>> find(Map<String, Object> filter) {
            return registry.find(filter);
        }

        public void add(String type, String key, Map<String, Object>... arguments) {
            // get bundle : context.getContext().eval("js", "")
            registry.add(type, key, arguments);
        }

        public void addOrReplace(String type, String key, Map<String, Object>... arguments) {
            registry.add(type, key, arguments);
        }

        public void remove(String type, String key) {
            registry.remove(type, key);
        }
    }
}
