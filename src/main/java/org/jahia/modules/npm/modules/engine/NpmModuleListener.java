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

import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.modules.npm.modules.engine.registrars.Registrar;
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

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Listener to execute scripts at activate/desactivate time
 */
@Component(immediate = true)
public class NpmModuleListener implements BundleListener {
    private static final Logger logger = LoggerFactory.getLogger(NpmModuleListener.class);
    public static final String SOURCES = "sources";
    public static final String MODULES = "/modules/";
    private GraalVMEngine engine;
    private final Collection<Registrar> registrars = new ArrayList<>();

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setEngine(GraalVMEngine engine) {
        this.engine = engine;
    }

    @Reference(service = Registrar.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
    public void addRegistrar(Registrar registrar) {
        for (Bundle bundle : engine.getNPMBundles()) {
            registrar.register(bundle);
        }

        registrars.add(registrar);
    }

    public void removeRegistrar(Registrar registrar) {
        registrars.remove(registrar);

        for (Bundle bundle : engine.getNPMBundles()) {
            registrar.unregister(bundle);
        }
    }

    @Activate
    public void activate(BundleContext context) {
        for (Bundle bundle : engine.getNPMBundles()) {
            engine.enableBundle(bundle);
        }

        context.addBundleListener(this);
    }

    @Deactivate
    public void deactivate(BundleContext context) {
        context.removeBundleListener(this);

        for (Bundle bundle : engine.getNPMBundles()) {
            engine.disableBundle(bundle);
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        try {
            Bundle bundle = event.getBundle();
            if (event.getType() == BundleEvent.RESOLVED) {
                copySources(bundle);
            } else if (event.getType() == BundleEvent.STARTED) {
                engine.enableBundle(bundle);
                for (Registrar registrar : registrars) {
                    registrar.register(bundle);
                }
            } else if (event.getType() == BundleEvent.STOPPED) {
                for (Registrar registrar : registrars) {
                    registrar.unregister(bundle);
                }
                engine.disableBundle(bundle);
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

}
