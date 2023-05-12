package org.jahia.modules.npm-modules-engine.views;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npm-modules-engine.jsengine.GraalVMEngine;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderException;
import org.jahia.services.render.Resource;
import org.jahia.services.render.View;
import org.jahia.services.render.scripting.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

public class JSScript implements Script {
    private static Logger logger = LoggerFactory.getLogger(JSScript.class);

    private JSView jsView;
    private GraalVMEngine graalVMEngine;

    public JSScript(JSView jsView, GraalVMEngine graalVMEngine) {
        this.jsView = jsView;
        this.graalVMEngine = graalVMEngine;
    }

    public interface Thenable {
        void then(Value onResolve, Value onReject);
    }

    @Override
    public String execute(Resource resource, RenderContext renderContext) throws RenderException {
        return graalVMEngine.doWithNewContext(contextProvider -> {

            Map<String, Object> viewValue = jsView.getValue(contextProvider);

            logger.debug("Calling JS Engine for view {}",resource.getTemplate());
            Object executionResult = Value.asValue(viewValue.get("render")).execute(resource, renderContext, ProxyObject.fromMap(viewValue));
            Value value = Value.asValue(executionResult);
            if (value.getMetaObject() != null && value.getMetaObject().getMetaSimpleName().equals("Promise")) {
                final StringBuilder result = new StringBuilder();
                logger.debug("Promise returned, returning placeholder");
                Consumer<Object> javaThen = v -> {
                    logger.debug("Promise resolved, setting value for future replacement");
                    result.append(v);
                };
                value.invokeMember("then", javaThen);
                Consumer<Object> javaCatch = e -> {
                    logger.error("Error when rendering promise", Value.asValue(e).as(PolyglotException.class));
                };
                value.invokeMember("catch", javaCatch);
                return result.toString();
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
