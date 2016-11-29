package com.bay1ts.bay.core;

import com.bay1ts.bay.route.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chenu on 2016/10/12.
 * 这个类主要被 静态引入.作为此框架的入口
 */
public class Bay {
    private static Logger logger = LoggerFactory.getLogger(Bay.class);
    private static Service getInstance() {
        return SingletonRouter.INSTANCE;
    }

    private static class SingletonRouter {
        private static final Service INSTANCE = new Service();
    }
    public static void listenAndStart() {
        try {
            getInstance().listenAndStart();
        } catch (Exception e) {
            logger.error("listen and start error: "+e.getMessage());
        }
    }
    public static void webSocket(String path,WebSocketAction action){
        getInstance().webSocket(path,action);
    }

    public static void halt(int status) {
        getInstance().halt(status);
    }

    public static void halt() {
        getInstance().halt();
    }

    public static void halt(String body) {
        getInstance().halt(body);
    }

    public static void halt(int status, String body) {
        getInstance().halt(status, body);
    }



    public static void staticResources(String floder) {
        getInstance().staticResources(floder);
    }

    public static void before(final String path, final Action action) {
        getInstance().before(path, action);
    }

    //暴露给外面的接口.最基本的  restful 接口

    // TODO: 2016/11/27 增加accepttype
    // TODO: 2016/11/27 增加动态路由,在接口上应该有所显示,这是动态添加的,应该将路由保存到 redis中去(在 集群环境,单机环境保存在内存中即可)
    public static void dynamicGet(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.get,path,action);
        }else {
            getInstance().get(path, action);
        }
    }
    public static void get(final String path, final Action action) {
        getInstance().get(path, action);
    }
    public static void dynamicPost(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.post,path,action);
        }else {
            getInstance().post(path, action);
        }
    }

    public static void post(final String path, final Action action) {
        getInstance().post(path, action);
    }
    public static void dynamicPut(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.put,path,action);
        }else {
            getInstance().put(path, action);
        }
    }

    public static void put(final String path, final Action action) {
        getInstance().put(path, action);
    }

    public static void dynamicPatch(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.patch,path,action);
        }else {
            getInstance().patch(path, action);
        }
    }
    public static void patch(final String path, final Action action) {
        getInstance().patch(path, action);
    }
    public static void dynamicDelete(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.delete,path,action);
        }else {
            getInstance().delete(path, action);
        }
    }
    public static void delete(final String path, final Action action) {
        getInstance().delete(path, action);
    }

    public static void dynamicHead(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.head,path,action);
        }else {
            getInstance().head(path, action);
        }
    }

    public static void head(final String path, final Action action) {
        getInstance().head(path, action);
    }
    public static void dynamicTrace(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.trace,path,action);
        }else {
            getInstance().trace(path, action);
        }
    }

    public static void trace(final String path, final Action action) {
        getInstance().trace(path, action);
    }
    public static void dynamicConnect(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.connect,path,action);
        }else {
            getInstance().connect(path, action);
        }
    }

    public static void connect(final String path, final Action action) {
        getInstance().connect(path, action);
    }

    public static void dynamicOptions(String path,Action action){
        if (Config.instance().isEnableScale()){
            getInstance().dynamicAdd(HttpMethod.options,path,action);
        }else {
            getInstance().options(path, action);
        }
    }

    public static void options(final String path, final Action action) {
        getInstance().options(path, action);
    }

    public static TreeNode NSGet(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.get, path, action);
    }

    public static TreeNode NSPost(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.post, path, action);
    }

    public static TreeNode NSPut(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.put, path, action);
    }

    public static TreeNode NSPatch(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.patch, path, action);
    }

    public static TreeNode NSDelete(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.delete, path, action);
    }

    public static TreeNode NSHead(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.head, path, action);
    }

    public static TreeNode NSTrace(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.trace, path, action);
    }

    public static TreeNode NSConnect(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.connect, path, action);
    }

    public static TreeNode NSOptions(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.options, path, action);
    }

    public static TreeNode NSBefore(String path, Action action) {
        //not sure
        return getInstance().NSAdd(HttpMethod.before, path, action);
    }


    public static TreeNode newNameSpace(String path, TreeNode... routeEntries) {
        return getInstance().newNameSpace(path, routeEntries);
    }


    public static void NSRoute(TreeNode... treeNodes) {
        getInstance().NSRoute(treeNodes);
    }

}
