package org.jahia.modules.npmplugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.graalvm.polyglot.Value;
import org.jahia.modules.npmplugins.jsengine.GraalVMEngine;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener to execute scripts at activate/desactivate time
 */
@Component(immediate = true)
public class JSInitListener implements BundleListener {

    private GraalVMEngine engine;
    private Map<String, Value> unregisters = new HashMap<>();

    @Reference
    public void setEngine(GraalVMEngine engine) {
        this.engine = engine;
    }

    @Activate
    public void activate(BundleContext context) {
        context.addBundleListener(this);
    }

    @Deactivate
    public void deactivate(BundleContext context) {
        context.removeBundleListener(this);
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.STARTED) {
            try {
                Bundle bundle = event.getBundle();
                URL url = bundle.getResource("package.json");
                if (url != null) {
                    String content = IOUtils.toString(url);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<?,?> m = mapper.readValue(content, Map.class);
                    Value register = engine.executeJs(bundle.getResource((String) m.get("main")));
                    Value unregister = register.getMember("default").execute();
                    unregisters.put(bundle.getSymbolicName(), unregister);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (event.getType() == BundleEvent.STOPPED) {
            if (unregisters.containsKey(event.getBundle().getSymbolicName())) {
                unregisters.remove(event.getBundle().getSymbolicName()).execute();
            }
        }
    }
}
