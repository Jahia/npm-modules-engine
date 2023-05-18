package org.jahia.modules.npm.modules.engine.helpers;

import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistryHelper {
    private final ContextProvider context;
    private final Registry registry;

    public RegistryHelper(ContextProvider context) {
        this.context = context;
        this.registry = new Registry(context);
    }

    public Object get(String type, String key) {
        return ProxyObject.fromMap(registry.get(type, key));
    }

    public List<Object> find(Map<String, Object> filter) {
        return registry.find(filter).stream().map(ProxyObject::fromMap).collect(Collectors.toList());
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

    public Registry getRegistry() {
        return registry;
    }

}
