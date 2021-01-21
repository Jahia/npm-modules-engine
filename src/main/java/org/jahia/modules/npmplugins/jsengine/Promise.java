package org.jahia.modules.npmplugins.jsengine;

import org.graalvm.polyglot.Value;

/**
 * An arbitrary "thenable" interface. Used to expose Java methods to JavaScript
 * Promise objects.
 */
@FunctionalInterface
public interface Promise {
    void then(Value onResolve, Value onReject);
}
