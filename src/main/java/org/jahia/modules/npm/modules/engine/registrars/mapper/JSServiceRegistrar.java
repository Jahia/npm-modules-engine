package org.jahia.modules.npm.modules.engine.registrars.mapper;

import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Map;

public interface JSServiceRegistrar<K> {

    ServiceRegistration<K> register(BundleContext bundleContext, Map<String, Object> jsService, GraalVMEngine engine);
}
