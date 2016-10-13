package com.bay1ts.bay.core;

import com.bay1ts.bay.utils.Utils;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class ServletContextImpl implements ServletContext {

//    private static final Logger log = LoggerFactory
//            .getLogger(ServletContextImpl.class);

    private static ServletContextImpl instance;

    private Map<String, Object> attributes;

    private String servletContextName;

    public static ServletContextImpl get() {
        if (instance == null)
            instance = new ServletContextImpl();

        return instance;
    }

    private ServletContextImpl() {
//        super("Netty Servlet Bridge");
    }

    @Override
    public Object getAttribute(String name) {
        return attributes != null ? attributes.get(name) : null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return Utils.enumerationFromKeys(attributes);
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public int getMajorVersion() {
        return 2;
    }

    @Override
    public int getMinorVersion() {
        return 4;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return ServletContextImpl.class.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return ServletContextImpl.class.getResourceAsStream(path);
    }

    @Override
    public String getServerInfo() {
//        return super.getOwnerName();
        return null;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Enumeration getInitParameterNames() {
        return null;
    }

    @Override
    public void log(String msg) {
//        log.info(msg);
        // TODO: 2016/10/13 log
    }

    @Override
    public void log(Exception exception, String msg) {
//        log.error(msg, exception);
        // TODO: 2016/10/13
    }

    @Override
    public void log(String message, Throwable throwable) {
//        log.error(messagesage, throwable);
    }

    @Override
    public void removeAttribute(String name) {
        if (this.attributes != null)
            this.attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        if (this.attributes == null)
            this.attributes = new HashMap<String, Object>();

        this.attributes.put(name, object);
    }

    @Override
    public String getServletContextName() {
        return this.servletContextName;
    }

    void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        throw new IllegalStateException(
                "Deprecated as of Java Servlet API 2.1, with no direct replacement!");
    }

    @Override
    public Enumeration getServletNames() {
        throw new IllegalStateException(
                "Method 'getServletNames' deprecated as of Java Servlet API 2.0, with no replacement.");
    }

    @Override
    public Enumeration getServlets() {
        throw new IllegalStateException(
                "Method 'getServlets' deprecated as of Java Servlet API 2.0, with no replacement.");
    }

    @Override
    public ServletContext getContext(String uripath) {
        return this;
    }

    @Override
    public String getMimeType(String file) {
        return Utils.getMimeType(file);

    }

    @Override
    public Set getResourcePaths(String path) {
        throw new IllegalStateException(
                "Method 'getResourcePaths' not yet implemented!");
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
//        Collection<ServletConfiguration> colls = ServletBridgeWebapp.get().getWebappConfig().getServletConfigurations();
//        HttpServlet servlet = null;
//        for (ServletConfiguration configuration : colls) {
//            if (configuration.getConfig().getServletName().equals(name)) {
//                servlet = configuration.getHttpComponent();
//            }
//        }
//
//        return new RequestDispatcherImpl(name, null, servlet);
        return null;
    }

    @Override
    public String getRealPath(String path) {
        if ("/".equals(path)) {
            try {
                File file = File.createTempFile("netty-servlet-bridge", "");
                file.mkdirs();
                return file.getAbsolutePath();
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Method 'getRealPath' not yet implemented!");
            }
        } else {
            throw new IllegalStateException(
                    "Method 'getRealPath' not yet implemented!");
        }
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
//        Collection<ServletConfiguration> colls = ServletBridgeWebapp.get().getWebappConfig().getServletConfigurations();
//        HttpServlet servlet = null;
//        String servletName = null;
//        for (ServletConfiguration configuration : colls) {
//            if (configuration.matchesUrlPattern(path)) {
//                servlet = configuration.getHttpComponent();
//                servletName = configuration.getHttpComponent().getServletName();
//            }
//        }
//
//        return new RequestDispatcherImpl(servletName, path, servlet);
        return null;
    }

}
