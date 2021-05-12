package org.jahia.modules.npmplugins.views;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderException;
import org.jahia.services.render.Resource;
import org.jahia.services.render.View;
import org.jahia.services.render.scripting.Script;

import java.util.Map;
import java.util.function.Consumer;

public class JSScript implements Script {
    private JSView jsView;
    private GraalVMEngine graalVMEngine;

    public JSScript(JSView jsView, GraalVMEngine graalVMEngine) {
        this.jsView = jsView;
        this.graalVMEngine = graalVMEngine;
    }

    @Override
    public String execute(Resource resource, RenderContext renderContext) throws RenderException {
        return graalVMEngine.doWithContext(contextProvider -> {

            Map<String, Object> viewValue = jsView.getValue(contextProvider);

            Object executionResult = Value.asValue(viewValue.get("render")).execute(resource, renderContext, ProxyObject.fromMap(viewValue));
            Value value = Value.asValue(executionResult);
            if (value.getMetaObject() != null && value.getMetaObject().getMetaSimpleName().equals("Promise")) {
                StringBuilder buf = new StringBuilder();
                Consumer<Object> javaThen = v -> buf.append(v.toString());
                value.invokeMember("then", javaThen);
                value.invokeMember("catch", javaThen);
                return buf.toString();
            } else {
                return value.asString();
            }
        });
    }

    @Override
    public View getView() {
        return jsView;
    }
}
