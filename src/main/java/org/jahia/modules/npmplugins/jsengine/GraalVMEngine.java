package org.jahia.modules.npmplugins.jsengine;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Base JS engine based on GraalVM
 */
@Component(service = GraalVMEngine.class, immediate = true)
public class GraalVMEngine {
    public static final String JS = "js";

    private final Engine sharedEngine = Engine.newBuilder().option("inspect", "9229").build();

    private ThreadLocal<ContextProvider> jsContext;
    private ContextProvider jsContextProvider;
    private Set<Context> all;

    private List<JSGlobalVariable> globals = new ArrayList<>();

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

        jsContext = ThreadLocal.withInitial(this::createContextProvider);
    }

    private ContextProvider createContextProvider() {
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
        return executeJs(code, null);
    }

    /**
     * Execute JS code from URL with additional bindings
     */
    public Value executeJs(URL code, Map<String, Object> bindings) throws IOException {
        ContextProvider cx = getContextProvider();
        return cx.doWithinLock(() -> {
            Map<String, Object> oldBindings = new HashMap<>();
            Value jsBindings = cx.getContext().getBindings(JS);
            try {
                if (bindings != null) {
                    bindings.forEach((key, value) -> {
                        oldBindings.put(key, jsBindings.getMember(key));
                        jsBindings.putMember(key, value);
                    });
                }
                Source source = Source.newBuilder(JS, code)
                        .cached(false)
                        .uri(new URI(code.toExternalForm()))
                        .build();
                return cx.getContext().eval(source);
            } catch (URISyntaxException e) {
                throw new IOException(e);
            } finally {
                oldBindings.forEach(jsBindings::putMember);
            }
        });
    }

    /**
     * Execute inline JS code
     */
    public Value executeJs(String code) {
        ContextProvider cx = getContextProvider();
        return cx.doWithinLock(() -> cx.getContext().eval(JS, code));
    }

    public ContextProvider getContextProvider() {
        return jsContext.get();
    }
}
