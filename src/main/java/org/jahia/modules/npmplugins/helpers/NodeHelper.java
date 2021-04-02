package org.jahia.modules.npmplugins.helpers;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.services.content.JCRNodeWrapper;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

public class NodeHelper {

    private ContextProvider context;

    public NodeHelper(ContextProvider context) {
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
