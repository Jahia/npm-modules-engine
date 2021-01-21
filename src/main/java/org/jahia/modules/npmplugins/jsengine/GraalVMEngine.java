package org.jahia.modules.npmplugins.jsengine;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Base JS engine based on GraalVM
 */
@Component(service = GraalVMEngine.class, immediate = true)
public class GraalVMEngine {
    private static final String JS = "js";

    private final Engine sharedEngine = Engine.newBuilder().build();

    private ThreadLocal<ContextProvider> jsContext;
    private Set<Context> all;

    private List<JSGlobalVariable> globals = new ArrayList<JSGlobalVariable>();

    @Reference(service = JSGlobalVariable.class, policy = ReferencePolicy.STATIC, cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
    public void bindVariable(JSGlobalVariable global) {
        globals.add(global);
    }

    public void unbindVariable(JSGlobalVariable global) {
        globals.remove(global);
    }

    @Activate
    public void activate(BundleContext bundleContext) {
        all = new HashSet<>();
        jsContext = ThreadLocal.withInitial(() -> {
            Context context = Context.newBuilder(JS)
                    .allowHostClassLookup(s -> true)
                    .allowHostAccess(HostAccess.ALL)
                    .allowPolyglotAccess(PolyglotAccess.ALL)
                    .allowIO(true)
                    .engine(sharedEngine).build();

            all.add(context);

            ContextProvider contextProvider = new ContextProvider(context);

            Map<String, Object> helpers = new HashMap<>();
            for (JSGlobalVariable global : globals) {
                helpers.put(global.getName(), global.getInstance(contextProvider));
            }
            context.getBindings(JS).putMember("jahiaHelpers", ProxyObject.fromMap(helpers));

            return contextProvider;
        });
    }

    @Deactivate
    public void deactivate() {
        for (Context context : all) {
            context.close();
        }
    }

    /**
     * Execute JS code from URL
     */
    public Value executeJs(URL code) throws IOException {
        ContextProvider cx = getContextProvider();
        return cx.doWithinLock(() -> {
            Source source = Source.newBuilder(JS, code)
                    .cached(false)
                    .mimeType("application/javascript+module")
                    .build();
            return cx.getContext().eval(source);
        });
    }

    /**
     * Execute inline JS code
     */
    public Value executeJs(String code) throws IOException {
        ContextProvider cx = getContextProvider();
        return cx.doWithinLock(() -> cx.getContext().eval(JS, code));
    }

    public ContextProvider getContextProvider() {
        return jsContext.get();
    }
}
