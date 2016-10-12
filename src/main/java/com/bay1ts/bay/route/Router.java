package com.bay1ts.bay.route;

import com.bay1ts.bay.Action;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;


/**
 * Created by chenu on 2016/9/3.
 * 一个优化方案.当 注册 method 为get的路由时,不放到list里.因为在遍历list耗时较多.可以讲几种分别放到几个 map中.按path来找对应的 action
 *
 */
public class Router {
    protected Routes routes;
    public  void get(final String path, final Action action){
        addRoute("get",RouteImpl.create(path,action));
    }

    // TODO: 2016/10/12  得有post等等全部的method
    public void addRoute(String httpMethod,RouteImpl route){
        routes.add(httpMethod,route);
        //此处有优化可能. 参看 文档注释
    }

    public static  Action getAction(String path){
        System.out.println("正在请求 路径 "+path +"的action");
        Action action=actionMap.get(path);
        if (null==action){
            System.out.println("log: 404 not found for "+path);
            action=new Action() {
                public FullHttpResponse handle(FullHttpRequest request, FullHttpResponse response) {
                    response.setStatus(HttpResponseStatus.NOT_FOUND);
                    return response.replace(Unpooled.copiedBuffer("404 not found",CharsetUtil.UTF_8));
                }
            };
        }
        return action;
    }
}
