package com.bay1ts.bay.route;

import com.bay1ts.bay.core.Request;
import io.netty.handler.codec.http.FullHttpRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by chenu on 2016/10/14.
 */
public class StaticRouteImpl {
    private String path;
    private List<String> welcomeFiles;

    public StaticRouteImpl(String path, List<String> welcomeFiles) {
        this.path = path;
        this.welcomeFiles = welcomeFiles;
    }
    public AbstractFileResolvingResource getResource(Request request) throws MalformedURLException {
        String servletPath;
        String pathInfo;
//        boolean included = request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;
        // TODO: 2016/10/14 对request.attribute方法存疑.所以此处需要测试
        boolean included=request.attribute("javax.servlet.include.request_uri")!=null;

        if (included) {
            servletPath = (String) request.attribute("javax.servlet.include.servlet_path");
            pathInfo = (String) request.attribute("javax.servlet.include.path_info");

            if (servletPath == null && pathInfo == null) {
                // TODO: 2016/10/14 下面两个方法的值不确定
                servletPath = request.servletPath();
                pathInfo = request.pathInfo();
            }
        } else {
            // TODO: 2016/10/14 方法需要测试
            servletPath = request.servletPath();
            pathInfo = request.pathInfo();//文件名
        }

        String pathInContext = addPaths(servletPath, pathInfo);
        return getResource(pathInContext);
    }

    private AbstractFileResolvingResource getResource(String pathInContext) {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        try {
            path = UriPath.canonical(path);

            final String addedPath = addPaths(baseResource, path);

            ClassPathResource resource = new ClassPathResource(addedPath);

            if (resource.exists() && resource.getFile().isDirectory()) {
                if (welcomeFile != null) {
                    resource = new ClassPathResource(addPaths(resource.getPath(), welcomeFile));
                } else {
                    //  No welcome file configured, serve nothing since it's a directory
                    resource = null;
                }
            }

            return (resource != null && resource.exists()) ? resource : null;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getClass().getSimpleName() + " when trying to get resource. " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Add two URI path segments.
     * Handles null and empty paths, path and query params (eg ?a=b or
     * ;JSESSIONID=xxx) and avoids duplicate '/'
     *
     * @param segment1 URI path segment (should be encoded)
     * @param segment2 URI path segment (should be encoded)
     * @return Legally combined path segments.
     */
    public static String addPaths(String segment1, String segment2) {
        if (segment1 == null || segment1.length() == 0) {
            if (segment1 != null && segment2 == null) {
                return segment1;
            }
            return segment2;
        }
        if (segment2 == null || segment2.length() == 0) {
            return segment1;
        }

        int split = segment1.indexOf(';');
        if (split < 0) {
            split = segment1.indexOf('?');
        }
        if (split == 0) {
            return segment2 + segment1;
        }
        if (split < 0) {
            split = segment1.length();
        }

        StringBuilder buf = new StringBuilder(segment1.length() + segment2.length() + 2);
        buf.append(segment1);

        if (buf.charAt(split - 1) == '/') {
            if (segment2.startsWith(SLASH)) {
                buf.deleteCharAt(split - 1);
                buf.insert(split - 1, segment2);
            } else {
                buf.insert(split, segment2);
            }
        } else {
            if (segment2.startsWith(SLASH)) {
                buf.insert(split, segment2);
            } else {
                buf.insert(split, '/');
                buf.insert(split + 1, segment2);
            }
        }

        return buf.toString();
    }

    public String path() {
        return path;
    }

    public List<String> welcomeFiles() {
        return welcomeFiles;
    }
}
