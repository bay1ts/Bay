/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bay1ts.bay.core;

import java.io.IOException;


import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

/**
 * Provides functionality for modifying the response
 *
 * @author Per Wendel
 */
public class Response {

    /**
     * The logger.
     */

    private FullHttpResponse response;
//    private HttpServletResponse response;
    private String body;
    private boolean isRedirected=false;

    protected Response() {
        // Used by wrapper
    }

    public boolean isRedirected(){
        return this.isRedirected;
    }

    public Response(FullHttpResponse response) {
        this.response = response;
    }


    /**
     * Sets the status code for the
     *
     * @param statusCode the status code
     */
    public void status(int statusCode) {
        response.setStatus(HttpResponseStatus.valueOf(statusCode));
    }

    /**
     * Returns the status code
     *
     * @return the status code
     */
    public int status() {
        return response.status().code();
    }

    /**
     * Sets the content type for the response
     *
     * @param contentType the content type
     */
    public void type(String contentType) {
        response.headers().set("Content-Type",contentType);
    }

    /**
     * Returns the content type
     *
     * @return the content type
     */
    public String type() {
        return response.headers().get("Content-Type");
    }

    /**
     * Sets the body
     *
     * @param body the body
     */
    public void body(String body) {
        this.body = body;
    }

    /**
     * returns the body
     *
     * @return the body
     */
    public String body() {
        return this.body;
    }

    /**
     * @return the raw response object handed in by Jetty
     */
    public FullHttpResponse raw() {
        return response;
    }

    /**
     * Trigger a browser redirect
     *
     * @param location Where to redirect
     */
    public void redirect(String location) {
        this.isRedirected=true;
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("Redirecting ({} {} to {}", "Found", HttpServletResponse.SC_FOUND, location);
//        }
//        try {
            response.headers().set("LOCATION",location);
            //// TODO: 2016/10/12 存疑
//        } catch (IOException ioException) {
//            LOG.warn("Redirect failure", ioException);
//        }
    }

    /**
     * Trigger a browser redirect with specific http 3XX status code.
     *
     * @param location       Where to redirect permanently
     * @param httpStatusCode the http status code
     */
    public void redirect(String location, int httpStatusCode) {
        this.isRedirected=true;
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("Redirecting ({} to {}", httpStatusCode, location);
//        }
//        response.setStatus(httpStatusCode);
//        response.setHeader("Location", location);
//        response.setHeader("Connection", "close");
        response.setStatus(HttpResponseStatus.valueOf(httpStatusCode));
        response.headers().set("Location",location);
        response.headers().set("Connection","close");
        // TODO: 2016/10/12 存疑.下面的五行没有实现
//        try {
//            response.sendError(httpStatusCode);
//        } catch (IOException e) {
//            LOG.warn("Exception when trying to redirect permanently", e);
//        }
    }

    /**
     * Adds/Sets a response header
     *
     * @param header the header
     * @param value  the value
     */
    public void header(String header, String value) {
        response.headers().set(header,value);
//        response.addHeader(header, value);
    }

    /**
     * Adds not persistent cookie to the response.
     * Can be invoked multiple times to insert more than one cookie.
     *
     * @param name  name of the cookie
     * @param value value of the cookie
     */
    public void cookie(String name, String value) {
        cookie(name, value, -1, false);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one cookie.
     *
     * @param name   name of the cookie
     * @param value  value of the cookie
     * @param maxAge max age of the cookie in seconds (negative for the not persistent cookie,
     *               zero - deletes the cookie)
     */
    public void cookie(String name, String value, int maxAge) {
        cookie(name, value, maxAge, false);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one cookie.
     *
     * @param name    name of the cookie
     * @param value   value of the cookie
     * @param maxAge  max age of the cookie in seconds (negative for the not persistent cookie, zero - deletes the cookie)
     * @param secured if true : cookie will be secured
     */
    public void cookie(String name, String value, int maxAge, boolean secured) {
        cookie(name, value, maxAge, secured, false);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one cookie.
     *
     * @param name     name of the cookie
     * @param value    value of the cookie
     * @param maxAge   max age of the cookie in seconds (negative for the not persistent cookie, zero - deletes the cookie)
     * @param secured  if true : cookie will be secured
     * @param httpOnly if true: cookie will be marked as http only
     */
    public void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        cookie("", name, value, maxAge, secured, httpOnly);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one cookie.
     *
     * @param path    path of the cookie
     * @param name    name of the cookie
     * @param value   value of the cookie
     * @param maxAge  max age of the cookie in seconds (negative for the not persistent cookie, zero - deletes the cookie)
     * @param secured if true : cookie will be secured
     */
    public void cookie(String path, String name, String value, int maxAge, boolean secured) {
        cookie(path, name, value, maxAge, secured, false);
    }

    /**
     * Adds cookie to the response. Can be invoked multiple times to insert more than one cookie.
     *
     * @param path     path of the cookie
     * @param name     name of the cookie
     * @param value    value of the cookie
     * @param maxAge   max age of the cookie in seconds (negative for the not persistent cookie, zero - deletes the cookie)
     * @param secured  if true : cookie will be secured
     * @param httpOnly if true: cookie will be marked as http only
     */
    public void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        Cookie cookie = new DefaultCookie(name,value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secured);
        cookie.setHttpOnly(httpOnly);
        response.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
    }

    /**
     * Removes the cookie.
     *
     * @param name name of the cookie
     */
    public void removeCookie(String name) {
//        Cookie cookie = new Cookie(name, "");
//        Cookie cookie=response.headers().get("Cookie");
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
        // TODO: 2016/10/12 不知道怎么实现 API不熟啊
    }
}
