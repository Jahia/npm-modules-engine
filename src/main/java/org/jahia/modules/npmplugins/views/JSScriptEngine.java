package org.jahia.modules.npmplugins.views;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.services.render.BundleView;
import org.jahia.services.render.scripting.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * Script engine for jahia views, using rendering function
 */
public class JSScriptEngine implements ScriptEngine {
    private static final Logger logger = LoggerFactory.getLogger(JSScriptEngine.class);

    protected JSScriptEngineFactory factory;
    protected GraalVMEngine graalVMEngine;

    public JSScriptEngine(JSScriptEngineFactory factory, GraalVMEngine graalVMEngine) {
        this.factory = factory;
        this.graalVMEngine = graalVMEngine;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return null;
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        try {
            // Ignore the script value passed in reader, rather read again from the URL
            reader.close();

            String result = eval(context.getBindings(ScriptContext.ENGINE_SCOPE));

            context.getWriter().write(result);
            return result;
        } catch (Exception e) {
            logger.error("Cannot execute JS", e);
            try {
                context.getWriter().write(e.getMessage());
            } catch (IOException ioException) {
                //
            }
            return e.getMessage();
        }
    }

    private String eval(Bindings bindings) throws IOException {
        BundleView view = (BundleView) ((Script) bindings.get("script")).getView();
        Value object = graalVMEngine.executeJs(view.getBundle().getResource(view.getResource()));

        if (object != null) {
            if (object.getMetaObject().getMetaSimpleName().equals("Function")) {
                return graalVMEngine.getContextProvider().doWithinLock(() -> object.execute(ProxyObject.fromMap(bindings)).asString());
            } else if (object.getMetaObject().getMetaSimpleName().equals("AsyncFunction")) {
                return graalVMEngine.getContextProvider().doWithinLock(() -> {
                    Value jsPromise = object.execute(ProxyObject.fromMap(bindings));
                    StringBuilder buf = new StringBuilder();
                    Consumer<Object> javaThen = (value) -> buf.append(value.toString());
                    jsPromise.invokeMember("then", javaThen);
                    jsPromise.invokeMember("catch", javaThen);
                    return buf.toString();
                });
            } else {
                return object.toString();
            }
        } else {
            return "null";
        }
    }

    @Override
    public Object eval(String script) throws ScriptException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object eval(Reader reader) throws ScriptException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object eval(String script, Bindings n) throws ScriptException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object eval(Reader reader, Bindings n) throws ScriptException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bindings getBindings(int scope) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {
        //
    }

    @Override
    public Bindings createBindings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScriptContext getContext() {
        return new SimpleScriptContext();
    }

    @Override
    public void setContext(ScriptContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }
}
