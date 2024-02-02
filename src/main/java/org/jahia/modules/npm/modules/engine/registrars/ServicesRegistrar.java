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
package org.jahia.modules.npm.modules.engine.registrars;

import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.modules.npm.modules.engine.registrars.mapper.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.util.*;

@Component(service = Registrar.class, immediate = true)
public class ServicesRegistrar implements Registrar {

    private BundleContext bundleContext;

    private GraalVMEngine graalVMEngine;

    private final Map<Bundle, Collection<ServiceRegistration<?>>> registrations = new HashMap<>();

    private static final Map<String, JSServiceRegistrar<?>> servicesRegistrar = new HashMap<>();
    static {
        servicesRegistrar.put("renderFilter", new JSRenderFilterRegistrar());
        servicesRegistrar.put("action", new JSActionRegistrar());
        servicesRegistrar.put("choiceListInitializer", new JSChoiceListInitializerRegistrar());
        servicesRegistrar.put("cacheKeyPartGenerator", new JSCacheKeyPartGeneratorRegistrar());
        servicesRegistrar.put("moduleGlobalObject", new JSModuleGlobalObjectRegistrar());
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setGraalVMEngine(GraalVMEngine graalVMEngine) {
        this.graalVMEngine = graalVMEngine;
    }

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void register(Bundle bundle) {
        List<Map<String, Object>> services = graalVMEngine.doWithContext(contextProvider -> {
            Map<String, Object> filter = new HashMap<>();
            filter.put("type", "service");
            filter.put("bundleKey", bundle.getSymbolicName());
            return contextProvider.getRegistry().find(filter);
        });

        Set<ServiceRegistration<?>> set = new HashSet<>();
        registrations.put(bundle, set);
        for (Map<String, Object> service : services) {
            if (servicesRegistrar.containsKey(service.get("serviceType").toString())) {
                JSServiceRegistrar<?> serviceRegistrar = servicesRegistrar.get(service.get("serviceType").toString());
                set.add(serviceRegistrar.register(bundleContext, service, graalVMEngine));
            }
        }
    }

    @Override
    public void unregister(Bundle bundle) {
        Collection<ServiceRegistration<?>> set = registrations.remove(bundle);
        if (set != null) {
            for (ServiceRegistration<?> registration : set) {
                if (bundleContext.getService(registration.getReference()) instanceof JSServiceClosable) {
                    ((JSServiceClosable) bundleContext.getService(registration.getReference())).close();
                }
                registration.unregister();
            }
        }
    }
}
