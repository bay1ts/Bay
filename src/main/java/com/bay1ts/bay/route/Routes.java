package com.bay1ts.bay.route;

import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.HttpMethod;
import com.bay1ts.bay.route.match.RouteMatch;
import com.bay1ts.bay.utils.MimeParse;

import java.util.*;

/**
 * Created by chenu on 2016/10/12.
 */
public class Routes {

    private List<RouteEntry> routes;
    public static Routes create(){
        return new Routes();
    }

    protected Routes() {
        routes = new ArrayList<>();
    }
    public Routes(List<RouteEntry> routes) {
        this.routes = routes;
    }

    /**
     * 注册路由的接口
     * @param httpMethod
     * @param route
     */
    public void add(String httpMethod,RouteImpl route){
        String path=route.getPath();
        HttpMethod method;
        try {
            method=HttpMethod.valueOf(httpMethod);
        }catch (IllegalArgumentException e){
            // TODO: 2016/10/12 记录日志
            return;
        }
        addRoute(method,path,route.getAcceptType(),route.getAction());
    }

    /**
     * 注册路由的核心实现
     * @param method
     * @param path
     * @param acceptType
     * @param action
     */
    private void addRoute(HttpMethod method, String path, String acceptType, Action action) {
        RouteEntry entry=new RouteEntry();
        entry.httpMethod=method;
        entry.path=path;
        entry.acceptedType=acceptType;
        entry.action=action;
        // TODO: 2016/10/12 记录日志啊,这里是真要加那啥了
        routes.add(entry);
        //下面这个刚开始没看懂
        //是个 showing all the mapped routes
        // TODO: 2016/10/12  package spark.route.Routes.java line 190
    }


    /**
     * 很明显这里用的httpmethod ,path,acceptType做的路由签名.这里用来  匹配
      * @param httpMethod
     * @param path
     * @param acceptType
     * @return
     */
    public RouteMatch find(HttpMethod httpMethod,String path,String acceptType){
        List<RouteEntry> routeEntries=findActionForRequestedRoute(httpMethod,path);
        RouteEntry entry=findActionWithGivenAcceptType(routeEntries,acceptType);
        return entry!=null?new RouteMatch(entry.action,entry.path,path,acceptType):null;
    }

    /**
     * Finds multiple targets for a requested route.
     *
     * @param httpMethod the http method
     * @param path       the route path
     * @param acceptType the accept type
     * @return the targets
     */
    public List<RouteMatch> findMultiple(HttpMethod httpMethod, String path, String acceptType) {
        List<RouteMatch> matchSet = new ArrayList<>();
        List<RouteEntry> routeEntries = findActionsForRequestedRoute(httpMethod, path);

        for (RouteEntry routeEntry : routeEntries) {
            if (acceptType != null) {
                String bestMatch = MimeParse.bestMatch(Arrays.asList(routeEntry.acceptedType), acceptType);

                if (routeWithGivenAcceptType(bestMatch)) {
                    matchSet.add(new RouteMatch(routeEntry.action, routeEntry.path, path, acceptType));
                }
            } else {
                matchSet.add(new RouteMatch(routeEntry.action, routeEntry.path, path, acceptType));
            }
        }

        return matchSet;
    }


    //attition 下面的慎用
    /**
     * Removes a particular route from the collection of those that have been previously routed.
     * Search for a previously established routes using the given path and removes any matches that are found.
     *
     * @param path the route path
     * @return <tt>true</tt> if this a matching route has been previously routed
     * @throws java.lang.IllegalArgumentException if <tt>path</tt> is null or blank
     * @since 2.2
     */
    public boolean remove(String path) {
//        if (StringUtils.isEmpty(path)) {
        if ("".equals(path)||path==null){
            throw new IllegalArgumentException("path cannot be null or blank");
        }

        return removeRoute((HttpMethod) null, path);
    }
    public void clear() {
        routes.clear();
//        RouteOverview.routes.clear();
    }
    private boolean removeRoute(HttpMethod httpMethod, String path) {
        List<RouteEntry> forRemoval = new ArrayList<>();

        for (RouteEntry routeEntry : routes) {
            HttpMethod httpMethodToMatch = httpMethod;

            if (httpMethod == null) {
                // Use the routeEntry's HTTP method if none was given, so that only path is used to match.
                httpMethodToMatch = routeEntry.httpMethod;
            }

            if (routeEntry.matches(httpMethodToMatch, path)) {
                // TODO: 2016/10/12 打日志 正删除呢
                forRemoval.add(routeEntry);
            }
        }

        return routes.removeAll(forRemoval);
    }

    private List<RouteEntry> findActionsForRequestedRoute(HttpMethod httpMethod, String path) {
        List<RouteEntry> matchSet = new ArrayList<RouteEntry>();
        for (RouteEntry entry : routes) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }
    private List<RouteEntry> findActionForRequestedRoute(HttpMethod httpMethod, String path) {
        List<RouteEntry> matchSet=new ArrayList<>();
        for (RouteEntry entry: routes){
            if (entry.matches(httpMethod,path)){
                matchSet.add(entry);
            }
        }
        return matchSet;
    }

    private RouteEntry findActionWithGivenAcceptType(List<RouteEntry> routeMatches, String acceptType) {
        if (acceptType != null && routeMatches.size() > 0) {
            Map<String, RouteEntry> acceptedMimeTypes = getAcceptedMimeTypes(routeMatches);
            String bestMatch = MimeParse.bestMatch(acceptedMimeTypes.keySet(), acceptType);

            if (routeWithGivenAcceptType(bestMatch)) {
                return acceptedMimeTypes.get(bestMatch);
            } else {
                return null;
            }
        } else {
            if (routeMatches.size() > 0) {
                return routeMatches.get(0);
            }
        }

        return null;
    }
    private boolean routeWithGivenAcceptType(String bestMatch) {
        return !MimeParse.NO_MIME_TYPE.equals(bestMatch);
    }
    private Map<String, RouteEntry> getAcceptedMimeTypes(List<RouteEntry> routes) {
        Map<String, RouteEntry> acceptedTypes = new HashMap<>();

        for (RouteEntry routeEntry : routes) {
            if (!acceptedTypes.containsKey(routeEntry.acceptedType)) {
                acceptedTypes.put(routeEntry.acceptedType, routeEntry);
            }
        }

        return acceptedTypes;
    }
}



