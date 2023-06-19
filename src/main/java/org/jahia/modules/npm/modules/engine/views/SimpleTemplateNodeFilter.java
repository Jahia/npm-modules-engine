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
        setPriority(20.5f);
        setSkipOnConfigurations("wrappedcontent,wrapper");
        setApplyOnTemplateTypes("html,html-.*");
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        Script script = (Script) renderContext.getRequest().getAttribute("script");

        if (resource.getNode().hasProperty(TEMPLATE_NAME)) {
            String oldTemplate = resource.getTemplate();
            try {
                if ("default".equals(resource.getTemplate()) && resource.getNode().hasProperty(TEMPLATE_NAME)) {
                    resource.setTemplate(resource.getNode().getProperty(TEMPLATE_NAME).getString());
                }
                script = service.resolveScript(resource, renderContext);
                renderContext.getRequest().setAttribute("script", script);
            } finally {
                resource.setTemplate(oldTemplate);
            }
        }

        if (Boolean.parseBoolean(script.getView().getProperties().getProperty("template"))) {
            chain.pushAttribute(renderContext.getRequest(), "inWrapper", Boolean.TRUE);
            chain.pushAttribute(renderContext.getRequest(), "skipWrapper", Boolean.TRUE);
            chain.pushAttribute(renderContext.getRequest(), "templateSet", Boolean.TRUE);
        }
        return null;
    }

}
