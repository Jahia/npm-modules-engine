package org.jahia.modules.npm.modules.engine.helpers;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.services.content.JCRNodeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

public class NodeHelper {
    private static final Logger logger = LoggerFactory.getLogger(NodeHelper.class);

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
            logger.error("Cannot get node",e);
        }
        return v;
    }
}
