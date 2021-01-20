package org.jahia.modules.npmplugins.views;

import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.jahia.services.render.scripting.bundle.Configurable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Script engine factory for jahia views, using rendering function
 * Use
 */
public class JSScriptEngineFactory implements ScriptEngineFactory, Configurable {
    private static Logger logger = LoggerFactory.getLogger(JSScriptEngineFactory.class);

    public static final String ENGINE_NAME = "Jahia views JS Engine";
    public static final List<String> EXTENSIONS = Arrays.asList("jjs");
    public static final String VERSION = "1.0";
    public static final String LANGUAGE_NAME = "javascript";
    public static final String LANGUAGE_VERSION = "1.0";
    protected ServiceTracker<GraalVMEngine, GraalVMEngine> serviceTracker;

    @Override
    public String getEngineName() {
        return ENGINE_NAME;
    }

    @Override
    public String getEngineVersion() {
        return VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public List<String> getMimeTypes() {
        return Collections.emptyList();
        // return Collections.singletonList("text/javascript");
    }

    @Override
    public List<String> getNames() {
        return Collections.emptyList();
        // return Collections.singletonList("javascript");
    }

    @Override
    public String getLanguageName() {
        return LANGUAGE_NAME;
    }

    @Override
    public String getLanguageVersion() {
        return LANGUAGE_VERSION;
    }

    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        return null;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return null;
    }

    @Override
    public String getProgram(String... statements) {
        return null;
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new JSScriptEngine(this, serviceTracker.getService());
    }

    @Override
    public void configurePreRegistration(Bundle bundle) {
        logger.info("JSScriptEngineFactory.configurePreRegistration");
        final BundleContext bundleContext = bundle.getBundleContext();
        serviceTracker = new ServiceTracker<>(bundleContext, GraalVMEngine.class, null);
        serviceTracker.open();
    }

    @Override
    public void destroy(Bundle bundle) {
        logger.info("JSScriptEngineFactory.destroy");
        serviceTracker.close();
    }

    @Override
    public void configurePreScriptEngineCreation() {
        logger.info("JSScriptEngineFactory.configurePreScriptEngineCreation");
    }
}
