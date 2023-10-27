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
package org.jahia.modules.npm.modules.engine.views;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderException;
import org.jahia.services.render.Resource;
import org.jahia.services.render.View;
import org.jahia.services.render.scripting.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JSScript implements Script {
    private static final Logger logger = LoggerFactory.getLogger(JSScript.class);

    private final JSView jsView;
    private final GraalVMEngine graalVMEngine;

    public JSScript(JSView jsView, GraalVMEngine graalVMEngine) {
        this.jsView = jsView;
        this.graalVMEngine = graalVMEngine;
    }

    public interface Thenable {
        void then(Value onResolve, Value onReject);
    }

    @Override
    public String execute(Resource resource, RenderContext renderContext) throws RenderException {
        return graalVMEngine.doWithContext(contextProvider -> {
            Map<String, Object> viewValue = jsView.getValue(contextProvider);

            logger.debug("Calling JS Engine for view {}", resource.getTemplate());
            Object executionResult = Value.asValue(viewValue.get("render")).execute(resource, renderContext, ProxyObject.fromMap(viewValue));
            Value value = Value.asValue(executionResult);
            return value.asString();
        });
    }

    @Override
    public View getView() {
        return jsView;
    }
}
