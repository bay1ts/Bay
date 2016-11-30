package com.bay1ts.bay.route.match;

import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.Response;
import com.bay1ts.bay.core.HttpMethod;
import com.bay1ts.bay.route.MemoryRoutes;
import com.bay1ts.bay.route.RouteStore;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by chenu on 2016/10/12.
 */
public final class RouteContext {

    /**
     * Creates a RouteContext
     */


    private RouteStore routeMatcher;
    private FullHttpRequest httpRequest;
    private String uri;
    private String acceptType;
    private Body body;
    private Request request;
    private Response response;
    private HttpMethod httpMethod;

    public static RouteContext create() {
        return new RouteContext();
    }

    private RouteContext() {
        // hidden
    }

    public RouteStore routeMatcher() {
        return routeMatcher;
    }

    public RouteContext withMatcher(RouteStore routeMatcher) {
        this.routeMatcher = routeMatcher;
        return this;
    }
    public RouteContext withRequest(Request request){
        this.request=request;
        return this;
    }

    public RouteContext withHttpRequest(FullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        return this;
    }

    public RouteContext withAcceptType(String acceptType) {
        this.acceptType = acceptType;
        return this;
    }

    public RouteContext withBody(Body body) {
        this.body = body;
        return this;
    }


    public RouteContext withUri(String uri) {
        this.uri = uri;
        return this;
    }


    public RouteContext withResponse(Response response) {
        this.response = response;
        return this;
    }

    public RouteContext withHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }
    public Request request(){
        return this.request;
    }
    public FullHttpRequest httpRequest() {
        return httpRequest;
    }

    public String uri() {
        return uri;
    }

    public String acceptType() {
        return acceptType;
    }

    public Body body() {
        return body;
    }


    public Response response() {
        return response;
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

}

