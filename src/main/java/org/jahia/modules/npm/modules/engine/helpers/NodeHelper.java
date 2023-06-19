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

    private final ContextProvider context;

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
            logger.error("Cannot get node", e);
        }
        return v;
    }
}
