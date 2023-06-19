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
package org.jahia.modules.npm.modules.engine.jsengine;

import org.graalvm.polyglot.Context;
import org.jahia.modules.npm.modules.engine.helpers.Registry;
import org.jahia.modules.npm.modules.engine.helpers.RegistryHelper;
import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ContextProvider {

    private boolean isActive = true;
    private final int version;
    private final Context context;

    private final Collection<Bundle> registeredBundles = new ArrayList<>();
    private final Map<String, Object> helpers = new HashMap<>();

    public ContextProvider(Context cx, int version) {
        this.context = cx;
        this.version = version;
    }

    public Context getContext() {
        return context;
    }

    public int getVersion() {
        return version;
    }

    public Collection<Bundle> getRegisteredBundles() {
        return registeredBundles;
    }

    public Map<String, Object> getHelpers() {
        return helpers;
    }

    public Registry getRegistry() {
        return ((RegistryHelper) getHelpers().get("registry")).getRegistry();
    }

    public void close() {
        context.close();
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }
}
