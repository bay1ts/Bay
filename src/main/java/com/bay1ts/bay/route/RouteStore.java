package com.bay1ts.bay.route;

import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.HttpMethod;
import com.bay1ts.bay.route.match.RouteMatch;

import java.util.List;

/**
 * Created by chenu on 2016/11/27.
 */
public interface RouteStore {
    void add(String httpMethod,RouteImpl route);
    RouteMatch find(HttpMethod httpMethod, String path, String acceptType);
    List<RouteMatch> findMultiple(HttpMethod httpMethod, String path, String acceptType);
    boolean removeRoute(HttpMethod httpMethod, String path);
    void clear();
}
