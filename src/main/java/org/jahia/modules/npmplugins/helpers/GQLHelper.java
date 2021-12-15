package org.jahia.modules.npmplugins.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.polyglot.Value;
import org.jahia.modules.npmplugins.jsengine.ContextProvider;
import org.jahia.modules.npmplugins.jsengine.Promise;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.render.RenderContext;
import org.jahia.services.securityfilter.PermissionService;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.util.*;

public class GQLHelper {
    private ContextProvider context;
    private HttpServlet servlet;

    public GQLHelper(ContextProvider context) {
        this.context = context;
    }

    public Promise executeQuery(Map parameters) {
        return (onResolve, onReject) -> {
            // convert JSON string to Map
            try {
                Value js = executeQuerySync(parameters);
                onResolve.execute(js);
            } catch (Exception e) {
                onReject.execute(e.getMessage());
            }
        };
    }

    public Value executeQuerySync(Map parameters) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("query", (String) parameters.get("query"));
        params.put("operationName", (String) parameters.get("operationName"));
        if (parameters.containsKey("variables")) {
            if (parameters.get("variables") instanceof String) {
                params.put("variables", (String) parameters.get("variables"));
            } else {
                params.put("variables", mapper.writeValueAsString(parameters.get("variables")));
            }
        }
        StringWriter out = new StringWriter();

        PermissionService permissionService = BundleUtils.getOsgiService(PermissionService.class, null);
        permissionService.addScopes(Collections.singleton("graphql"), null);

        RenderContext renderContext = (RenderContext)  parameters.get("renderContext");
        HttpServletRequest req = renderContext == null ? new HttpServletRequestMock(params) : new HttpServletRequestWrapper(renderContext.getRequest()) {
            public String getParameter(String name) {
                if (params.containsKey(name)) {
                    return (String) params.get(name);
                }
                return super.getParameter(name);
            }
        };
        System.out.println(params);
        servlet.service(req, new HttpServletResponseMock(out));
        System.out.println(out);
        Value js = context.getContext().eval("js", "(" + out + ")");
        return js;
    }

    @Inject
    @OSGiService(service = HttpServlet.class,
        filter = "(component.name=graphql.kickstart.servlet.OsgiGraphQLHttpServlet)")
    public void setServlet(HttpServlet servlet) {
        this.servlet = (HttpServlet) servlet;
    }

    private static class HttpServletRequestMock implements HttpServletRequest {
        private final Map<String, String> params;

        public HttpServletRequestMock(Map<String, String> params) {
            this.params = params;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return "GET";
        }

        @Override
        public String getPathInfo() {
            return "/";
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return (String) params.get(name);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {

        }

        @Override
        public void removeAttribute(String name) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

    }

    private static class HttpServletResponseMock implements HttpServletResponse {
        private final StringWriter out;

        public HttpServletResponseMock(StringWriter out) {
            this.out = out;
        }

        @Override
        public void addCookie(Cookie cookie) {

        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeURL(String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return null;
        }

        @Override
        public String encodeUrl(String url) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return null;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {

        }

        @Override
        public void sendError(int sc) throws IOException {

        }

        @Override
        public void sendRedirect(String location) throws IOException {

        }

        @Override
        public void setDateHeader(String name, long date) {

        }

        @Override
        public void addDateHeader(String name, long date) {

        }

        @Override
        public void setHeader(String name, String value) {

        }

        @Override
        public void addHeader(String name, String value) {

        }

        @Override
        public void setIntHeader(String name, int value) {

        }

        @Override
        public void addIntHeader(String name, int value) {

        }

        @Override
        public void setStatus(int sc) {

        }

        @Override
        public void setStatus(int sc, String sm) {

        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {

        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public void setContentType(String type) {

        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(out);
        }

        @Override
        public void setContentLength(int len) {

        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void setBufferSize(int size) {

        }

        @Override
        public void flushBuffer() throws IOException {

        }

        @Override
        public void resetBuffer() {

        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public void setLocale(Locale loc) {

        }
    }

}
