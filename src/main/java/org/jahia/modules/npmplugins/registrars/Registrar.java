package org.jahia.modules.npmplugins.registrars;

import org.jahia.modules.npmplugins.helpers.RegistryHelper;
import org.osgi.framework.Bundle;

public interface Registrar {

    void register(RegistryHelper registry, Bundle bundle);

    void unregister(RegistryHelper registry, Bundle bundle);
}
