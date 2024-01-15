package org.jahia.modules.npm.modules.engine.js;

import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.JSGlobalVariableFactory;
import org.osgi.service.component.annotations.Component;

@Component(service = {JSGlobalVariableFactory.class}, immediate = true)
public class JSServerEngineGlobalVariableFactory implements JSGlobalVariableFactory {
    @Override
    public String getName() {
        return "jsServerEngineLibraryBuilder";
    }

    @Override
    public Object getObject(ContextProvider context) {
        return new JSServerEngineLibraryBuilder(context);
    }
}
