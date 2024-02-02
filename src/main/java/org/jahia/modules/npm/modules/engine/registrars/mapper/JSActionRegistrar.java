package org.jahia.modules.npm.modules.engine.registrars.mapper;

import org.graalvm.polyglot.Value;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.npm.modules.engine.jsengine.ContextProvider;
import org.jahia.modules.npm.modules.engine.jsengine.GraalVMEngine;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class JSActionRegistrar implements JSServiceRegistrar<Action>{

    @Override
    public ServiceRegistration<Action> register(BundleContext bundleContext, Map<String, Object> jsService, GraalVMEngine engine) {
        return bundleContext.registerService(Action.class, new JSAction(jsService, engine), new Hashtable<>());
    }

    public static class JSAction extends Action {
        private final GraalVMEngine engine;
        private final String key;

        public JSAction(Map<String, Object> value, GraalVMEngine engine) {
            this.engine = engine;
            this.key = (String) value.get("key");
            if (value.containsKey("name")) {
                setName(value.get("name").toString());
            }
            if (value.containsKey("requireAuthenticatedUser")) {
                setRequireAuthenticatedUser((Boolean) value.get("requireAuthenticatedUser"));
            }
            if (value.containsKey("requiredMethods")) {
                setRequiredMethods(value.get("requiredMethods").toString());
            }
            if (value.containsKey("requiredPermission")) {
                setRequiredPermission(value.get("requiredPermission").toString());
            }
            if (value.containsKey("requiredWorkspace")) {
                setRequiredWorkspace(value.get("requiredWorkspace").toString());
            }
        }

        @Override
        public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> map, URLResolver urlResolver) throws Exception {
            return engine.doWithContext(contextProvider -> {
                Value jsResult = Value.asValue(getJSValues(contextProvider).get("doExecute")).execute(httpServletRequest, renderContext, resource, jcrSessionWrapper, map, urlResolver);
                return convertResult(jsResult);
            });
        }

        private ActionResult convertResult(Value jsResult) {
            int resultCode = jsResult.getMember("resultCode").asInt();
            String url = jsResult.hasMember("url") ? jsResult.getMember("url").asString() : null;
            boolean isAbsoluteUrl = jsResult.hasMember("absoluteUrl") && jsResult.getMember("absoluteUrl").asBoolean();
            JSONObject jsonObject = jsResult.hasMember("json") ? new JSONObject(jsResult.getMember("json").as(Map.class)) : null;
            return new ActionResult(resultCode, url, isAbsoluteUrl, jsonObject);
        }

        private Map<String, Object> getJSValues(ContextProvider contextProvider) {
            return contextProvider.getRegistry().get("service", key);
        }
    }
}
