package com.bay1ts.bay.core;

import com.bay1ts.bay.route.RouteImpl;
import com.bay1ts.bay.route.Routes;
import com.bay1ts.bay.route.StaticMatcher;
import com.bay1ts.bay.route.TreeNode;

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
    public void NSRoute(TreeNode ... treeNodes){
        if (treeNodes.length > 1) {
            for (TreeNode treeNode : treeNodes) {
                Iter(treeNode);
            }
        } else {
            Iter(treeNodes[0]);
        }
    }

    public void Iter(TreeNode treeNode) {
        if (treeNode.isLeaf()) {
            System.out.println(treeNode.getPassedPath() + treeNode.getObj());
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
            chindren.setParentId(treeNode.getSelfId());
            treeNode.addChildNode(chindren);
            treeNode.setNodeName("hehe");
        }
        return treeNode;
    }


//    public final class StaticResources{
//        public void location(String folder){
//            //// TODO: 2016/10/13 参看原  service文件451行
//        }
//    }
}
