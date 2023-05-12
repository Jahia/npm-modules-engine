package org.jahia.modules.npm-modules-engine.registrars;

import org.jahia.modules.npm-modules-engine.helpers.Registry;
import org.jahia.modules.npm-modules-engine.jsengine.GraalVMEngine;
import org.osgi.framework.Bundle;

public interface Registrar {

    void register(Registry registry, Bundle bundle, GraalVMEngine engine);

    void unregister(Registry registry, Bundle bundle);
}
