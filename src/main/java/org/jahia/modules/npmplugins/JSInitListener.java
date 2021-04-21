package org.jahia.modules.npmplugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRTemplate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Listener to execute scripts at activate/desactivate time
 */
@Component(immediate = true)
public class JSInitListener implements BundleListener {
    private static final Logger logger = LoggerFactory.getLogger(JSInitListener.class);
    public static final String SOURCES = "sources";
    public static final String MODULES = "/modules/";
    private GraalVMEngine engine;

    @Reference
    public void setEngine(GraalVMEngine engine) {
        this.engine = engine;
    }

    @Activate
    public void activate(BundleContext context) {
        for (Bundle bundle : context.getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                enableBundle(bundle);
            }
        }
        context.addBundleListener(this);
    }

    @Deactivate
    public void deactivate(BundleContext context) {
        context.removeBundleListener(this);

        for (Bundle bundle : context.getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                disableBundle(bundle);
            }
        }
    }


    @Override
    public void bundleChanged(BundleEvent event) {
        try {
            Bundle bundle = event.getBundle();
            if (event.getType() == BundleEvent.RESOLVED) {
                copySources(bundle);
            } else if (event.getType() == BundleEvent.STARTED) {
                enableBundle(bundle);
            } else if (event.getType() == BundleEvent.STOPPED) {
                disableBundle(bundle);
            }
        } catch (Exception e) {
            logger.error("Cannot handle event", e);
        }
    }

    private void copySources(Bundle bundle) throws RepositoryException {
        URL url = bundle.getResource("package.json");
        if (url != null) {
            JCRTemplate.getInstance().doExecuteWithSystemSession(session -> {
                JahiaTemplatesPackage pkg = BundleUtils.getModule(bundle);
                JCRNodeWrapper n = session.getNode(MODULES + pkg.getIdWithVersion());
                if (n.hasNode(SOURCES)) {
                    n.getNode(SOURCES).remove();
                }
                JCRNodeWrapper sources = n.addNode(SOURCES, "jnt:moduleVersionFolder");
                try {
                    importResources(pkg, "/", sources);
                } catch (IOException e) {
                    throw new RepositoryException(e);
                }
                session.save();
                return null;
            });
        }
    }

    private void importResources(JahiaTemplatesPackage pkg, String path, JCRNodeWrapper node) throws IOException, RepositoryException {
        Resource[] r = pkg.getResources(path);
        for (Resource resource : r) {
            if (resource.contentLength() > 0) {
                node.uploadFile(resource.getFilename(), resource.getInputStream(), "text/plain");
            } else {
                importResources(pkg, resource.getURL().getPath(), node.addNode(resource.getFilename(), "jnt:folder"));
            }
        }
    }

    private void enableBundle(Bundle bundle) {
        try {
            URL url = bundle.getResource("package.json");
            if (url != null) {
                String content = IOUtils.toString(url);
                ObjectMapper mapper = new ObjectMapper();
                Map<?,?> json = mapper.readValue(content, Map.class);
                Map<?,?> jahia = (Map<?, ?>) json.get("jahia");
                if (jahia != null && jahia.containsKey("server")) {
                    String script = (String) jahia.get("server");
                    engine.addInitScript(bundle, script);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot enable bundle {}", bundle.getSymbolicName(), e);
        }
    }

    private void disableBundle(Bundle bundle) {
        try {
            engine.removeInitScript(bundle);
        } catch (Exception e) {
            logger.error("Cannot disable bundle {}", bundle.getSymbolicName(), e);
        }
    }
}
