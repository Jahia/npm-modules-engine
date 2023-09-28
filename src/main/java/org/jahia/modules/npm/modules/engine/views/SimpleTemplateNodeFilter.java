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

import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.jahia.services.render.scripting.Script;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service = RenderFilter.class)
public class SimpleTemplateNodeFilter extends AbstractFilter {

    public static final String TEMPLATE_NAME = "j:templateName";

    @Activate
    public void activate() {
        // Before Jahia cache system
        setPriority(15.9f);
        setApplyOnMainResource(true);
        setApplyOnTemplateTypes("html,html-.*");
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {

        // Bypass Jahia cache script resolution for generating attributes for cache keys
        // So that we are sure the cache key will be correct.
        Script script;
        if ("default".equals(resource.getTemplate()) && resource.getNode().hasProperty(TEMPLATE_NAME)) {
            try {
                // Handle template resolution for pages using NPM templates
                resource.setTemplate(resource.getNode().getProperty(TEMPLATE_NAME).getString());
                script = resource.getScript(renderContext);
            } finally {
                resource.setTemplate("default");
            }
        } else {
            script = resource.getScript(renderContext);
        }

        // in case view properties contains "template=true" it means it's a NPM template, so we setup some requests attributes
        // mostly to bypass Jahia core TemplateNodeFilter.
        if (Boolean.parseBoolean(script.getView().getProperties().getProperty("template"))) {
            chain.pushAttribute(renderContext.getRequest(), "inWrapper", Boolean.TRUE);
            chain.pushAttribute(renderContext.getRequest(), "skipWrapper", Boolean.TRUE);
            chain.pushAttribute(renderContext.getRequest(), "templateSet", Boolean.TRUE);
        }
        return null;
    }
}
