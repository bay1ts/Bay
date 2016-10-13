package com.bay1ts.bay.route.match;

import com.bay1ts.bay.core.Response;
import com.bay1ts.bay.route.HttpMethod;
import com.bay1ts.bay.route.Routes;

/**
 * Created by chenu on 2016/10/12.
 */
final class RouteContext {

    /**
     * Creates a RouteContext
     */
    static RouteContext create() {
        return new RouteContext();
    }

    private Routes routeMatcher;
    private HttpServletRequest httpRequest;
    private String uri;
    private String acceptType;
    private Body body;
    private RequestWrapper requestWrapper;
    private ResponseWrapper responseWrapper;
    private Response response;
    private HttpMethod httpMethod;

    private RouteContext() {
        // hidden
    }

    public Routes routeMatcher() {
        return routeMatcher;
    }

    public RouteContext withMatcher(Routes routeMatcher) {
        this.routeMatcher = routeMatcher;
        return this;
    }

    public RouteContext withHttpRequest(HttpServletRequest httpRequest) {
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

    public RouteContext withRequestWrapper(RequestWrapper requestWrapper) {
        this.requestWrapper = requestWrapper;
        return this;
    }

    public RouteContext withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public RouteContext withResponseWrapper(ResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
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

    public HttpServletRequest httpRequest() {
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

    public RequestWrapper requestWrapper() {
        return requestWrapper;
    }

    public ResponseWrapper responseWrapper() {
        return responseWrapper;
    }

    public Response response() {
        return response;
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

}

