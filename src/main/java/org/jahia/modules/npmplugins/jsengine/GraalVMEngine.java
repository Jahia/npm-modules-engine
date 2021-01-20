package org.jahia.modules.npmplugins.jsengine;

import org.graalvm.polyglot.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Base JS engine based on GraalVM
 */
@Component(service = GraalVMEngine.class, immediate = true)
public class GraalVMEngine {
    private static final String JS = "js";

    private final Engine sharedEngine = Engine.newBuilder().build();

    private ThreadLocal<ContextProvider> jsContext;
    private Set<Context> all;

    @Activate
    public void activate(BundleContext bundleContext) {
        all = new HashSet<>();
        jsContext = ThreadLocal.withInitial(() -> {
            Context context = Context.newBuilder(JS).allowHostAccess(HostAccess.ALL)
                    .allowPolyglotAccess(PolyglotAccess.ALL)
                    .allowIO(true)
                    .engine(sharedEngine).build();

            all.add(context);

            try {
                Source build = Source.newBuilder(JS, bundleContext.getBundle().getResource("serverjs/main.js")).mimeType("application/javascript+module").build();
                Value serverLibs = context.eval(build);
                context.getBindings(JS).putMember("serverLibs", serverLibs);
                return new ContextProvider(context, serverLibs);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Deactivate
    public void deactivate() {
        for (Context context : all) {
            context.close();
        }
    }

    /**
     * Submit a new request to the JavaScript engine. Returns a `CompletionStage`
     * instance that will complete when GraalVM JavaScript produces a value.
     */
    public Value executeJs(URL code) throws IOException {
        ContextProvider cx = jsContext.get();
        cx.getLock().lock();
        try {
            return cx.getContext().eval(Source.newBuilder(JS, code).mimeType("application/javascript+module").build());
        } finally {
            cx.getLock().unlock();
        }
    }

    public Value executeJs(String code) throws IOException {
        ContextProvider cx = jsContext.get();
        cx.getLock().lock();        try {
            return cx.getContext().eval(JS, code);
        } finally {
            cx.getLock().unlock();

        }
    }

    public Value getServerLibs() {
        return jsContext.get().getServerLibs();
    }

    private static class ContextProvider {

        private final Context context;
        private final ReentrantLock lock;
        private final Value serverLibs;

        ContextProvider(Context cx, Value serverLibs) {
            this.context = cx;
            this.serverLibs = serverLibs;
            this.lock = new ReentrantLock();
        }

        Context getContext() {
            return context;
        }

        public Value getServerLibs() {
            return serverLibs;
        }

        Lock getLock() {
            return lock;
        }
    }
}
