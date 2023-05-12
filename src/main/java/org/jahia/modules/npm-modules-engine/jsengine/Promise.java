package org.jahia.modules.npm-modules-engine.jsengine;

import org.graalvm.polyglot.Value;
import pl.touk.throwing.ThrowingSupplier;

/**
 * An arbitrary "thenable" interface. Used to expose Java methods to JavaScript
 * Promise objects.
 */
@FunctionalInterface
public interface Promise {
    void then(Value onResolve, Value onReject);

    static Promise promisify(ThrowingSupplier<Object, Exception> s) {
        return (onResolve, onReject) -> {
            try {
                onResolve.execute(s.get());
            } catch (Exception e) {
                onReject.execute(e.getMessage());
            }
        };
    }

}
