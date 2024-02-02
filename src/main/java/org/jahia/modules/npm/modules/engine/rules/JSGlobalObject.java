package org.jahia.modules.npm.modules.engine.rules;

import org.graalvm.polyglot.Value;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;

import java.util.Map;

public class JSGlobalObject {
    private final GraalVMEngine engine;
    private String key;

    public JSGlobalObject(String key, GraalVMEngine engine) {
        this.engine = engine;
        this.key = key;
    }

    public void call(String methodName, Object... args) {
        engine.doWithContext(contextProvider -> {
            Value.asValue(getJSValues(contextProvider).get("object")).getMember(methodName).execute(args);
            return null;
        });
    }

    private Map<String, Object> getJSValues(ContextProvider contextProvider) {
        return contextProvider.getRegistry().get("service", key);
    }
}