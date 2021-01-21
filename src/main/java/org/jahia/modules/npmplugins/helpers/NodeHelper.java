package org.jahia.modules.npmplugins.helpers;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.JSGlobalVariable;
import org.jahia.services.content.JCRNodeWrapper;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

@Component(service = JSGlobalVariable.class, immediate = true)
public class NodeHelper implements JSGlobalVariable {

    @Override
    public String getName() {
        return "nodeHelper";
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

        public Value getNode(JCRNodeWrapper node) {
            Value v = context.getContext().eval("JS", "{properties:{}, children:{}}");
            try {
                PropertyIterator ni = node.getProperties();
                while (ni.hasNext()) {
                    Property next = (Property) ni.next();
                    v.getMember("properties").putMember(next.getName(), next.getValue());
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
            return v;
        }
    }
}
