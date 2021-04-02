package org.jahia.modules.npmplugins.registrars;

import org.jahia.modules.npmplugins.helpers.Registry;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.osgi.framework.Bundle;

public interface Registrar {

    void register(Registry registry, Bundle bundle, GraalVMEngine engine);

    void unregister(Registry registry, Bundle bundle);
}
