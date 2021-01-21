package org.jahia.modules.npmplugins.jsengine;

import org.graalvm.polyglot.Context;
import pl.touk.throwing.ThrowingSupplier;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContextProvider {

    private final Context context;
    private final ReentrantLock lock;

    public ContextProvider(Context cx) {
        this.context = cx;
        this.lock = new ReentrantLock();
    }

    public Context getContext() {
        return context;
    }

    public Lock getLock() {
        return lock;
    }

    public <T,E extends Exception> T doWithinLock(ThrowingSupplier<T,E> f) throws E {
        lock.lock();

        try {
            return f.get();
        } finally {
            lock.unlock();
        }
    }

}
