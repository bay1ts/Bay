package com.bay1ts.bay.core;

import com.bay1ts.bay.route.*;

import java.util.List;


/**
 * Created by chenu on 2016/9/3.
 * 一个优化方案.当 注册 method 为get的路由时,不放到list里.因为在遍历list耗时较多.可以讲几种分别放到几个 map中.按path来找对应的 action
 *
 */
public class Service {
    private static Routes routes;
    private static StaticMatcher staticMatcher;
    public static Routes getRouterMatcher(){
        return routes;
    }
    public static StaticMatcher staticMatcher(){
        return staticMatcher;
    }
    protected Service(){
        routes=Routes.create();
        staticMatcher=new StaticMatcher();
    }
    public void staticResources(String res){
        staticMatcher.path(res);
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
    
    
    
    public void before(String path,Action action){
        addFilter(HttpMethod.before.name(), RouteImpl.create(path,action));
    }

    private void addFilter(String httpMethod, RouteImpl filter) {
        addRoute(httpMethod,filter);
    }
    public void addRoute(String httpMethod,RouteImpl route){
        routes.add(httpMethod,route);
        //此处有优化可能. 参看 文档注释
    }

    public void NSRoute(TreeNode ... treeNodes){
        if (treeNodes.length > 1) {
            for (TreeNode treeNode : treeNodes) {
                Iter(treeNode);
            }
        } else {
            Iter(treeNodes[0]);
        }
    }
    public TreeNode NSAdd(HttpMethod httpMethod, String path, Action action){
        RouteEntry routeEntry=new RouteEntry(httpMethod,path,null,action);
        TreeNode treeNode = new TreeNode();
        treeNode.setObj(path);
        treeNode.setRouteEntry(routeEntry);
        treeNode.setChildList(null);
        return treeNode;
    }

    public void Iter(TreeNode treeNode) {
        if (treeNode.isLeaf()) {
            // TODO: 2016/10/20 此处注册路由.单丝我发现这么一弄吧,把httpmethod信息弄没了
            treeNode.getRouteEntry().setPath(treeNode.getPassedPath() + treeNode.getObj());
            RouteEntry routeEntry=treeNode.getRouteEntry();
            addRoute(routeEntry.getHttpMethod().name(),RouteImpl.create(routeEntry.getPath(),routeEntry.getAction()));
//            System.out.println(treeNode.getPassedPath() + treeNode.getObj());
        } else {
            List<TreeNode> list = treeNode.getChildList();
            for (TreeNode node : list) {
                node.setPassedPath(node.getParentNode().getPassedPath() + node.getParentNode().getObj().toString());
                Iter(node);
            }
        }
    }
    public  TreeNode newNameSpace(String path, TreeNode... routeEntries) {
        TreeNode treeNode = new TreeNode();
        treeNode.setObj(path);
        for (TreeNode chindren : routeEntries) {
            chindren.setParentNode(treeNode);
            treeNode.addChildNode(chindren);
        }
        return treeNode;
    }
    public void halt() {
        throw new HaltException();
    }
    public void halt(int status) {
        throw new HaltException(status);
    }
    public void halt(String body) {
        throw new HaltException(body);
    }
    public void halt(int status, String body) {
        throw new HaltException(status, body);
    }


//    public final class StaticResources{
//        public void location(String folder){
//            //// TODO: 2016/10/13 参看原  service文件451行
//        }
//    }
}
