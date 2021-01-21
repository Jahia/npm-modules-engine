package org.jahia.modules.npmplugins.helpers;

import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.JSGlobalVariable;
import org.osgi.service.component.annotations.Component;

import java.util.Map;

@Component(service = JSGlobalVariable.class, immediate = true)
public class RegistryHelper implements JSGlobalVariable {

    @Override
    public String getName() {
        return "registry";
    }

    @Override
    public Object getInstance(ContextProvider context) {
        return new Instance(context);
    }

    public class Instance {
        private ContextProvider context;

        public Instance(ContextProvider context) {
            this.context = context;
        }

        public void add(String type, String key, Map<?,?>... arguments) {
            System.out.println("RegistryHelper.add");
        }

        public void addOrReplace(String type, String key, Map<?,?>... arguments) {
            System.out.println("RegistryHelper.addOrReplace");
        }

        public Map<?,?> get(String type, String key) {
            return null;
        }

        public void remove(String type, String key) {
            System.out.println("RegistryHelper.remove");
        }



    }
}
