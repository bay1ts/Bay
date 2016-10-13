package com.bay1ts.bay.route;

import com.bay1ts.bay.core.Action;


/**
 * Created by chenu on 2016/9/3.
 * 一个优化方案.当 注册 method 为get的路由时,不放到list里.因为在遍历list耗时较多.可以讲几种分别放到几个 map中.按path来找对应的 action
 *
 */
public class Router {
    //这个实例要 传给 handler 这特么怎么弄,只能单例了吗
    private static  Routes routes=Routes.create();

//    public void init(){
//        //initroutematcher
//        routes=
//
//    }
    public static Routes getRouterMatcher(){
        return routes;
    }

    public void addRoute(String httpMethod,RouteImpl route){
        routes.add(httpMethod,route);
        //此处有优化可能. 参看 文档注释

    }


    public  void get(final String path, final Action action){
        addRoute(HttpMethod.get.name(),RouteImpl.create(path,action));
    }
    public void post(String path, Action action) {
        addRoute(HttpMethod.post.name(),RouteImpl.create(path,action));
    }

    public void put(String path, Action action) {
        addRoute(HttpMethod.put.name(),RouteImpl.create(path,action));
    }

    public void patch(String path, Action action) {
        addRoute(HttpMethod.patch.name(),RouteImpl.create(path,action));
    }

    public void delete(String path, Action action) {
        addRoute(HttpMethod.delete.name(),RouteImpl.create(path,action));
    }

    public void head(String path, Action action) {
        addRoute(HttpMethod.head.name(),RouteImpl.create(path,action));
    }

    public void trace(String path, Action action) {
        addRoute(HttpMethod.trace.name(),RouteImpl.create(path,action));
    }

    public void connect(String path, Action action) {
        addRoute(HttpMethod.connect.name(),RouteImpl.create(path,action));
    }

    public void options(String path, Action action) {
        addRoute(HttpMethod.options.name(),RouteImpl.create(path,action));
    }
    // TODO: 2016/10/12 像beego学习,加上any
}
