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

import org.apache.commons.lang.StringUtils;
import org.jahia.bin.Jahia;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
import org.jahia.services.content.nodetypes.initializers.ChoiceListInitializer;
import org.jahia.services.content.nodetypes.initializers.ChoiceListInitializerService;
import org.jahia.services.content.nodetypes.initializers.ChoiceListValue;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.View;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;

import javax.jcr.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;

@Component(immediate = true)
public class SimpleTemplatesNodeChoiceListInitializer implements ChoiceListInitializer {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SimpleTemplatesNodeChoiceListInitializer.class);

    private ChoiceListInitializerService service;

    private ChoiceListInitializer oldInitializer;

    @Reference
    public void setService(ChoiceListInitializerService service) {
        this.service = service;
    }

    @Activate
    public void activate() {
        oldInitializer = service.getInitializers().remove("templatesNode");
        service.getInitializers().put("templatesNode", this);
    }

    @Deactivate
    public void deactivate() {
        service.getInitializers().put("templatesNode", oldInitializer);
    }

    @Override
    public List<ChoiceListValue> getChoiceListValues(ExtendedPropertyDefinition epd, String param, List<ChoiceListValue> previousValues, Locale locale, Map<String, Object> context) {
        List<ChoiceListValue> values = oldInitializer.getChoiceListValues(epd, param, previousValues, locale, context);
        new ArrayList<>();

        try {
            JCRNodeWrapper node = (JCRNodeWrapper) context.get("contextNode");
            ExtendedNodeType nodetype;
            if (node == null) {
                node = (JCRNodeWrapper) context.get("contextParent");
                nodetype = (ExtendedNodeType) context.get("contextType");
            } else {
                nodetype = node.getPrimaryNodeType();
            }

            JCRSiteNode site = node.getResolveSite();

            final JCRSessionWrapper session = site.getSession();

            // get default template
            String defaultTemplate = site.hasProperty("j:defaultTemplateName") ? site.getProperty("j:defaultTemplateName").getString() : null;

            List<ChoiceListValue> newValues = addTemplates(site, session, nodetype, defaultTemplate, epd, locale, context);

            values.addAll(0, newValues);
        } catch (RepositoryException e) {
            logger.error("Cannot get template", e);
        }

        return values;
    }

    private List<ChoiceListValue> addTemplates(JCRSiteNode site, JCRSessionWrapper session, ExtendedNodeType nodetype, String defaultTemplate, ExtendedPropertyDefinition propertyDefinition, Locale locale, Map<String, Object> context) throws RepositoryException {
        return RenderService.getInstance().getViewsSet(nodetype, site, "html").stream()
                .filter(v -> "true".equals(v.getProperties().getProperty("template")))
                .map(v -> new ChoiceListValue(v.getDisplayName(), getProperties(v, defaultTemplate), session.getValueFactory().createValue(v.getKey())))
                .sorted()
                .collect(Collectors.toList());
    }

    private Map<String, Object> getProperties(View view, String defaultTemplate) {
        Map<String, Object> props = new HashMap<>();
        if (StringUtils.equals(view.getKey(), defaultTemplate)) {
            props.put("defaultProperty", true);
        }

        String iconName = StringUtils.substringAfterLast(view.getPath(), "/").replace(".hbs", ".png");
        if (view.getModule().getBundle().findEntries("images", iconName, false) != null) {
            props.put("image", Jahia.getContextPath() + "/modules/" + view.getModule().getBundle().getSymbolicName() + "/images/" + iconName);
        }
        return props;
    }

}
