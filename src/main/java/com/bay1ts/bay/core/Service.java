package com.bay1ts.bay.core;

import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.route.HttpMethod;
import com.bay1ts.bay.route.RouteImpl;
import com.bay1ts.bay.route.Routes;


/**
 * Created by chenu on 2016/9/3.
 * 一个优化方案.当 注册 method 为get的路由时,不放到list里.因为在遍历list耗时较多.可以讲几种分别放到几个 map中.按path来找对应的 action
 *
 */
public class Service {
    private static Routes routes;
    private static String resLocation;
    public static Routes getRouterMatcher(){
        return routes;
    }
    public static String StaticResourcesLocation(){
        return resLocation;
    }
    protected Service(){
        routes=Routes.create();
    }
    public void staticResources(String res){
        // TODO: 2016/10/13 处理静态资源啊
        resLocation=res;
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


//    public final class StaticResources{
//        public void location(String folder){
//            //// TODO: 2016/10/13 参看原  service文件451行
//        }
//    }
}
