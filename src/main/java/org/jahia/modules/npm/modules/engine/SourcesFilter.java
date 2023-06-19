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
package org.jahia.modules.npm.modules.engine;

import org.apache.commons.lang3.StringUtils;
import org.jahia.api.Constants;
import org.jahia.bin.filters.AbstractServletFilter;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.templates.JahiaTemplateManagerService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component(immediate = true, service = AbstractServletFilter.class)
public class SourcesFilter extends AbstractServletFilter {
    private static final Logger logger = LoggerFactory.getLogger(SourcesFilter.class);

    private JahiaTemplateManagerService modulesService;
    private JCRTemplate jcrTemplate;

    @Reference
    public void setModulesService(JahiaTemplateManagerService modulesService) {
        this.modulesService = modulesService;
    }

    @Reference
    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Activate
    public void activate() {
        setUrlPatterns(new String[]{"/modules/*"});
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        String uri = httpRequest.getRequestURI();
        if (uri.endsWith(".css")) {
            String moduleName = StringUtils.substringBetween(uri, NpmModuleListener.MODULES, "/");
            String path = StringUtils.substringAfter(uri, NpmModuleListener.MODULES + moduleName);
            JahiaTemplatesPackage pack = modulesService.getTemplatePackageById(moduleName);
            if (pack != null) {
                try {
                    String value = jcrTemplate.doExecuteWithSystemSession(session -> {
                        String nodePath = NpmModuleListener.MODULES + moduleName + '/' + pack.getVersion().toString() + '/' + NpmModuleListener.SOURCES + path + '/' + Constants.JCR_CONTENT;
                        if (session.itemExists(nodePath)) {
                            return session.getNode(nodePath).getProperty("jcr:data").getString();
                        }

                        return null;
                    });

                    if (value != null) {
                        servletResponse.getWriter().write(value);
                        return;
                    }
                } catch (RepositoryException e) {
                    logger.error("Cannot read source for {}", uri, e);
                }
            }
        }

        filterChain.doFilter(httpRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Do nothing
    }
}
