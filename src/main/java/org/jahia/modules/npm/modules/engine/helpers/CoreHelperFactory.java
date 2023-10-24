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

import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.helpers.injector.OSGiServiceInjector;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.JSGlobalVariableFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component(service = {JSGlobalVariableFactory.class, CoreHelperFactory.class}, immediate = true)
public class CoreHelperFactory implements JSGlobalVariableFactory {
    private static final Logger logger = LoggerFactory.getLogger(CoreHelperFactory.class);

    @Override
    public String getName() {
        return "jahiaHelpers";
    }

    @Override
    public Object getObject(ContextProvider contextProvider) {
        Map<String,Object> helpers = new HashMap<>();
        helpers.put("registry", new RegistryHelper(contextProvider));
        helpers.put("render", new RenderHelper(contextProvider));
        helpers.put("gql", new GQLHelper(contextProvider));
        helpers.put("osgi", new OSGiHelper(contextProvider));
        helpers.put("node", new NodeHelper(contextProvider));

        for (Map.Entry<String, Object> entry : helpers.entrySet()) {
            try {
                OSGiServiceInjector.handleMethodInjection(entry.getValue());
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Cannot inject services for {} helper", entry.getKey(), e);
            }
        }

        return ProxyObject.fromMap(helpers);
    }
}
