package com.bay1ts.bay.route;

import com.bay1ts.bay.core.Action;

/**
 * Created by chenu on 2016/10/12.
 */
public class RouteImpl {
    static final String DEFAULT_ACCEPT_TYPE = "*/*";

    private String path;
    private String acceptType;
    private Action action;

    public RouteImpl(String path, String acceptType) {
        this.path=path;
        this.acceptType=acceptType;
    }

    public static RouteImpl create(String path, Action action) {
        return create(path,DEFAULT_ACCEPT_TYPE,action);
    }

    private static RouteImpl create(String path, String acceptType, Action action) {
        return new RouteImpl(path,acceptType,action);
    }

//    private static RouteImpl create(String path, String acceptType, Action action) {
//        return new RouteImpl(path,acceptType,action){
//            @Override
//            public Object handle(FullHttpRequest request, FullHttpResponse response) {
//                return action.handle(request,response);
//            }
//        };
//    }

    public RouteImpl(String path, String acceptType,Action action) {
        this(path,acceptType);
        this.action=action;
    }
//    public abstract Object handle(FullHttpRequest request, FullHttpResponse response);


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAcceptType() {
        return acceptType;
    }

    public void setAcceptType(String acceptType) {
        this.acceptType = acceptType;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
