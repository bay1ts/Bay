package com.bay1ts.bay.route;

import com.bay1ts.bay.Action;

import java.util.List;

/**
 * Created by chenu on 2016/10/12.
 */
public class Routes {

    private List<RouteEntry> routes;

    public Routes(List<RouteEntry> routes) {
        this.routes = routes;
    }
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
    }
}



