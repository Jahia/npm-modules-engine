package org.jahia.modules.npmplugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.graalvm.polyglot.Value;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npmplugins.helpers.OSGiHelper;
import org.jahia.modules.npmplugins.helpers.RegistryHelper;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.modules.npmplugins.registrars.Registrar;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRTemplate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import pl.touk.throwing.ThrowingSupplier;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Listener to execute scripts at activate/desactivate time
 */
@Component(immediate = true)
public class JSInitListener implements BundleListener {
    private static final Logger logger = LoggerFactory.getLogger(JSInitListener.class);
    private GraalVMEngine engine;
    private Collection<Registrar> registrars = new ArrayList<>();
    private RegistryHelper registryHelper;
    private Set<Bundle> bundles = new HashSet<>();
    private Map<String, Value> unregisters = new HashMap<>();

    @Reference
    public void setEngine(GraalVMEngine engine) {
        this.engine = engine;
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
    public void addRegistrar(Registrar registrar) {
        registrars.add(registrar);
    }

    public void removeRegistrar(Registrar registrar) {
        registrars.remove(registrar);
    }

    @Reference
    public void setRegistryHelper(RegistryHelper registryHelper) {
        this.registryHelper = registryHelper;
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
                JCRNodeWrapper n = session.getNode("/modules/" + pkg.getIdWithVersion());
                if (n.hasNode("sources")) {
                    n.getNode("sources").remove();
                }
                JCRNodeWrapper sources = n.addNode("sources", "jnt:moduleVersionFolder");
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
                    bundles.add(bundle);
                    String script = (String) jahia.get("server");
                    String source = OSGiHelper.loadResource(bundle, script);
                    Value register = engine.executeJs(source);
                    registryHelper.setCurrentRegisteringBundle(bundle);
                    try {
                        Value unregister = register.getMember("default").execute(bundle.getBundleContext());
                        unregisters.put(bundle.getSymbolicName(), unregister);
                    } finally {
                        registryHelper.setCurrentRegisteringBundle(null);
                    }

                    for (Registrar registrar : registrars) {
                        registrar.register(registryHelper, bundle);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot enable bundle {}", bundle.getSymbolicName(), e);
        }
    }

    private void disableBundle(Bundle bundle) {
        try {
            if (bundles.contains(bundle)) {
                Map<String, Object> filter = new HashMap<>();
                filter.put("bundle", bundle);

                for (Registrar registrar : registrars) {
                    registrar.unregister(registryHelper, bundle);
                }

                List<Map<String, Object>> toRemoveObjects = registryHelper.getRegistry().find(filter);
                for (Map<String, Object> toRemoveObject : toRemoveObjects) {
                    registryHelper.getRegistry().remove((String) toRemoveObject.get("type"), (String) toRemoveObject.get("key"));
                }

                if (unregisters.containsKey(bundle.getSymbolicName())) {
                    unregisters.remove(bundle.getSymbolicName()).execute();
                }
                bundles.remove(bundle);
            }
        } catch (Exception e) {
            logger.error("Cannot disable bundle {}", bundle.getSymbolicName(), e);
        }
    }
}
