package org.jahia.modules.npmplugins.views;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderException;
import org.jahia.services.render.Resource;
import org.jahia.services.render.View;
import org.jahia.services.render.scripting.Script;

import java.util.function.Consumer;

public class JSScript implements Script {
    private JSView jsView;

    public JSScript(JSView jsView) {
        this.jsView = jsView;
    }

    @Override
    public String execute(Resource resource, RenderContext renderContext) throws RenderException {
        ProxyObject proxy = ProxyObject.fromMap(jsView.getJsValue());
        Object object = Value.asValue(jsView.getJsValue().get("render")).execute(resource, renderContext, proxy);
        Value value = Value.asValue(object);
        if (value.getMetaObject() != null && value.getMetaObject().getMetaSimpleName().equals("Promise")) {
            StringBuilder buf = new StringBuilder();
            Consumer<Object> javaThen = v -> buf.append(v.toString());
            value.invokeMember("then", javaThen);
            value.invokeMember("catch", javaThen);
            return buf.toString();
        } else {
            return value.asString();
        }
    }

    @Override
    public View getView() {
        return jsView;
    }
}
