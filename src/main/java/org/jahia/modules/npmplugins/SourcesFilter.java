package org.jahia.modules.npmplugins;

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
        setUrlPatterns(new String[] {"/modules/*"});
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
            String moduleName = StringUtils.substringBetween(uri, JSInitListener.MODULES,"/");
            String path = StringUtils.substringAfter(uri, JSInitListener.MODULES + moduleName);
            JahiaTemplatesPackage pack = modulesService.getTemplatePackageById(moduleName);
            if (pack != null) {
                try {
                    String value = jcrTemplate.doExecuteWithSystemSession(session -> {
                        String nodePath = JSInitListener.MODULES + moduleName + '/' + pack.getVersion().toString() + '/' + JSInitListener.SOURCES + path + '/' + Constants.JCR_CONTENT;
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
