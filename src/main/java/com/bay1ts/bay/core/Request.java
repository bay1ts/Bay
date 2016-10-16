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

import com.bay1ts.bay.core.session.HttpSessionThreadLocal;
import com.bay1ts.bay.route.match.RouteMatch;
import com.bay1ts.bay.utils.IOUtils;
import com.bay1ts.bay.utils.SparkUtils;
import com.bay1ts.bay.utils.StringUtils;
import com.bay1ts.bay.utils.Utils;
import com.sun.xml.internal.bind.v2.TODO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.ssl.SslHandler;

import javax.servlet.http.HttpSession;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * Provides information about the HTTP request
 *
 * @author Per Wendel
 * bay1ts 修改
 */
public class Request {



    private static final String USER_AGENT = "user-agent";
    private Map<String, String> params;
    private List<String> splat;
    private QueryParamsMap queryMap;
    private FullHttpRequest fullHttpRequest;
    private QueryStringDecoder queryStringDecoder;
    private String queryString;
    private Session session = null;
    private boolean validSession = false;
    private Map<String, Object> attributes;
    /* Lazy loaded stuff */
    private String body = null;
    private byte[] bodyAsBytes = null;

    private Set<String> headers = null;

    //    request.body              # request body sent by the client (see below), DONE
    //    request.scheme            # "http"                                DONE
    //    request.path_info         # "/foo",                               DONE
    //    request.port              # 80                                    DONE
    //    request.request_method    # "GET",                                DONE
    //    request.query_string      # "",                                   DONE
    //    request.content_length    # length of request.body,               DONE
    //    request.media_type        # media type of request.body            DONE, content type?
    //    request.host              # "example.com"                         DONE
    //    request["SOME_HEADER"]    # value of SOME_HEADER header,          DONE
    //    request.user_agent        # user agent (used by :agent condition) DONE
    //    request.url               # "http://example.com/example/foo"      DONE
    //    request.ip                # client IP address                     DONE
    //    request.env               # raw env hash handed in by Rack,       DONE
    //    request.get?              # true (similar methods for other verbs)
    //    request.secure?           # false (would be true over ssl)
    //    request.forwarded?        # true (if running behind a reverse proxy)
    //    request.cookies           # hash of browser cookies,              DONE
    //    request.xhr?              # is this an ajax request?
    //    request.script_name       # "/example"
    //    request.form_data?        # false
    //    request.referrer          # the referrer of the client or '/'

    protected Request() {
        // Used by wrapper
    }

    /**
     * Constructor
     *
     * @param match   the route match
     * @param request the servlet request
     */
    public Request(RouteMatch match, FullHttpRequest request) {
        this.fullHttpRequest = request;
        this.queryStringDecoder=new QueryStringDecoder(request.uri());
        if (match!=null){
            //说明是routematch
            changeMatch(match);
        }

    }

    protected void changeMatch(RouteMatch match) {
        List<String> requestList = SparkUtils.convertRouteToList(match.getRequestURI());
        List<String> matchedList = SparkUtils.convertRouteToList(match.getMatchUri());

        params = getParams(requestList, matchedList);
        splat = getSplat(requestList, matchedList);
    }

    /**
     * Returns the map containing all route params
     *
     * @return a map containing all route params
     */
    public Map<String, String> params() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     *
     * @param param the param
     * @return null if the given param is null or not found
     */
    public String params(String param) {
        if (param == null) {
            return null;
        }

        if (param.startsWith(":")) {
            return params.get(param.toLowerCase()); // NOSONAR
        } else {
            return params.get(":" + param.toLowerCase()); // NOSONAR
        }
    }

    /**
     * @return an array containing the splat (wildcard) parameters
     */
    public String[] splat() {
        return splat.toArray(new String[splat.size()]);
    }

    /**
     * @return request method e.g. GET, POST, PUT, ...
     */
    public String requestMethod() {
//        return fullHttpRequest.getMethod();
        return fullHttpRequest.method().name();
    }

    /**
     * @return the scheme
     */
    public String scheme() {
        System.out.println("---------------=========");
//        fullHttpRequest.headers(HttpHeaderNames.)
        return isSecure()?"https":"http";
    }

    public boolean isSecure(){
        // TODO: 2016/10/16 bug found
        Channel channel=ChannelThreadLocal.get();
        //上一行返回的是空..为什么呢
        ChannelPipeline channelPipeline=channel.pipeline();
        channelPipeline.get(SslHandler.class);
        return ChannelThreadLocal.get().pipeline().get(SslHandler.class)!=null;
    }

    /**
     * @return the host
     */
    public String host() {
//        return fullHttpRequest.getHeader("host");
        return fullHttpRequest.headers().get(HttpHeaderNames.HOST);
    }

    /**
     * @return the user-agent
     */
    public String userAgent() {
        return fullHttpRequest.headers().get(HttpHeaderNames.USER_AGENT);
//        return fullHttpRequest.getHeader(USER_AGENT);
    }

    /**
     * @return the server port
     */
    public int port() {
        InetSocketAddress address=(InetSocketAddress) ChannelThreadLocal.get().localAddress();
        return address.getPort();
    }


    /**
     * @return the path info
     * Example return: "/example/foo"
     */
    public String pathInfo() {
        return queryStringDecoder.path();
//        return fullHttpRequest.getPathInfo();
    }

    /**
     * @return the servlet path
     * https://github.com/why2012/jNetty/blob/1.1.x/src/main/java/com/jnetty/core/request/HttpRequestFacade.java
     * line 247
     */
    public String servletPath() {
        // TODO: 2016/10/16 bug found
        return this.pathInfo();
//        return fullHttpRequest.getServletPath();
    }

    /**
     * @return the context path
     * https://github.com/why2012/jNetty/blob/1.1.x/src/main/java/com/jnetty/core/request/HttpRequestFacade.java
     * line 201
     */
    public String contextPath() {
        String uri=fullHttpRequest.uri();
        int slashIndex = uri.indexOf("/", 1);
        int queIndex = uri.indexOf("?", 0);
        if (slashIndex > queIndex && queIndex != -1) {
            slashIndex = -1;
        }
        return uri.substring(0, slashIndex == -1 ? (queIndex == -1 ? uri.length() : queIndex) : slashIndex);
//        return fullHttpRequest.getContextPath();
    }

    /**
     * @return the URL string
     */
    public String url() {
        return fullHttpRequest.uri();
    }

    /**
     * @return the content type of the body
     */
    public String contentType() {
        return fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    /**
     * @return the client's IP address
     */
    public String ip() {
        InetSocketAddress addr = (InetSocketAddress) ChannelThreadLocal.get()
                .remoteAddress();
        return addr.getAddress().getHostAddress();
//        return fullHttpRequest.getRemoteAddr();
    }

    /**
     * @return the request body sent by the client
     */
    public String body() {

        if (body == null) {

            body = StringUtils.toString(bodyAsBytes(), Utils.getCharsetFromContentType(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE)));
        }

        return body;
    }

    public byte[] bodyAsBytes() {
        if (bodyAsBytes == null) {
            readBodyAsBytes();
        }
        return bodyAsBytes;
    }

    private void readBodyAsBytes() {
        try {
//            bodyAsBytes = IOUtils.toByteArray(fullHttpRequest.getInputStream());
            ByteBuf buf=fullHttpRequest.content();
            //麻蛋不自信是正确的.这里没有初始化.会溢出的.
            bodyAsBytes=new byte[buf.readableBytes()];
            buf.readBytes(bodyAsBytes);
//            bodyAsBytes=IOUtils.toByteArray()
        } catch (Exception e) {
//            LOG.warn("Exception when reading body", e);
        }
    }

    /**
     * @return the length of request.body
     */
    public int contentLength() {
        return Integer.valueOf(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_LENGTH));
    }

    /**
     * Gets the query param
     *
     * @param queryParam the query parameter
     * @return the value of the provided queryParam
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    //// TODO: 2016/10/13 要是有俩怎么办.求测试
    public String queryParams(String queryParam) {
        String[] values = getParameterValues(queryParam);
        return values != null ? values[0] : null;
    }

    public String[] getParameterValues(String name) {
        List<String> values = queryStringDecoder.parameters().get(name);
        if (values == null || values.isEmpty())
            return null;
        return values.toArray(new String[values.size()]);
    }

    /**
     * Gets all the values of the query param
     * Example: query parameter 'id' from the following request URI: /hello?id=foo&amp;id=bar
     *
     * @param queryParam the query parameter
     * @return the values of the provided queryParam, null if it doesn't exists
     */
    public String[] queryParamsValues(String queryParam) {
//        return fullHttpRequest.getParameterValues(queryParam);
        List<String> values = this.queryStringDecoder.parameters().get(queryParam);
        if (values == null || values.isEmpty())
            return null;
        return values.toArray(new String[values.size()]);
    }

    /**
     * Gets the value for the provided header
     *
     * @param header the header
     * @return the value of the provided header
     */
    public String headers(String header) {
        return fullHttpRequest.headers().get(header);
    }

    /**
     * @return all query parameters
     */
    public Set<String> queryParams() {
//        return fullHttpRequest.getParameterMap().keySet();
        return this.queryStringDecoder.parameters().keySet();
    }

    /**
     *
     * @return all headers
     * 参考
     * https://github.com/why2012/jNetty/blob/1.1.x/src/main/java/com/jnetty/core/request/HttpRequestFacade.java
     */
    public Set<String> headers() {
        if (headers == null) {
            headers=fullHttpRequest.headers().names();
        }
        return headers;
    }

    /**
     * @return the query string
     * 参考 https://github.com/why2012/jNetty/blob/1.1.x/src/main/java/com/jnetty/core/request/HttpRequestFacade.java
     * line 204的实现方式
     */
    public String queryString() {
        if (queryString == null) {
            String[] uriArray = this.fullHttpRequest.uri().split("\\?", 2);
            if (uriArray.length >= 2) {
                queryString = uriArray[1];
            }
        }
        return queryString;
//        return fullHttpRequest.getQueryString();
    }

    /**
     * Sets an attribute on the request (can be fetched in filters/routes later in the chain)
     *
     * @param attribute The attribute
     * @param value     The attribute value
     */
    public void attribute(String attribute, Object value) {
        if (this.attributes == null)
            this.attributes = new HashMap<String, Object>();

        this.attributes.put(attribute, value);
    }

    /**
     * Gets the value of the provided attribute
     *
     * @param attribute The attribute value or null if not present
     * @param <T>       the type parameter.
     * @return the value for the provided attribute
     */
    public <T> T attribute(String attribute) {
        if (attributes != null)
            return (T)this.attributes.get(attribute);

        return null;
    }


    /**
     * @return all attributes
     */
    public Set<String> attributes() {
        Set<String> attrList = new HashSet<>();
        Enumeration<String> attributes = (Enumeration<String>) Utils.enumerationFromKeys(this.attributes);
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    /**
     * @return the raw HttpServletRequest object handed in by Jetty
     */
    public FullHttpRequest raw() {
        return fullHttpRequest;
    }

    /**
     * @return the query map
     */
    public QueryParamsMap queryMap() {
        initQueryMap();

        return queryMap;
    }

    /**
     * @param key the key
     * @return the query map
     */
    public QueryParamsMap queryMap(String key) {
        return queryMap().get(key);
    }

    private void initQueryMap() {
        if (queryMap == null) {
            queryMap = new QueryParamsMap(raw());
        }
    }

    /**
     * Returns the current session associated with this request,
     * or if the request does not have a session, creates one.
     *
     * @return the session associated with this request
     */
    public Session session() {
        if (session == null || !validSession) {
            validSession(true);
//            session = new Session(fullHttpRequest.getSession(), this);
            session = new Session(HttpSessionThreadLocal.getOrCreate(), this);
        }
        return session;
    }

    /**
     * Returns the current session associated with this request, or if there is
     * no current session and <code>create</code> is true, returns  a new session.
     *
     * @param create <code>true</code> to create a new session for this request if necessary;
     *               <code>false</code> to return null if there's no current session
     * @return the session associated with this request or <code>null</code> if
     * <code>create</code> is <code>false</code> and the request has no valid session
     */
    public Session session(boolean create) {
        if (session == null || !validSession) {
            //
            HttpSession sessionTemp = HttpSessionThreadLocal.get();
            if (sessionTemp == null && create) {
                sessionTemp = HttpSessionThreadLocal.getOrCreate();
            }
            //
            HttpSession httpSession = sessionTemp;
            if (httpSession != null) {
                validSession(true);
                session = new Session(httpSession, this);
            } else {
                session = null;
            }
        }
        return session;
    }

    /**
     * @return request cookies (or empty Map if cookies aren't present)
     */
    public Map<String, String> cookies() {
        Map<String, String> result = new HashMap<>();
        String cookieString=fullHttpRequest.headers().get(HttpHeaderNames.COOKIE);
        Set<Cookie> cookieSet=null;
        if (cookieString!=null){
            cookieSet=ServerCookieDecoder.STRICT.decode(cookieString);
        }
        if (cookieSet!=null&&!cookieSet.isEmpty()){
            for (Cookie cookie : cookieSet) {
                result.put(cookie.name(), cookie.value());
            }
        }
//        Cookie[] cookies = fullHttpRequest.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                result.put(cookie.name(), cookie.value());
//            }
//        }
        return result;
    }

    /**
     * Gets cookie by name.
     *
     * @param name name of the cookie
     * @return cookie value or null if the cookie was not found
     */
    public String cookie(String name) {
        String cookieString=fullHttpRequest.headers().get(HttpHeaderNames.COOKIE);
        Set<Cookie> cookieSet=null;
        if (cookieString!=null){
            cookieSet=ServerCookieDecoder.STRICT.decode(cookieString);
        }
        if (cookieSet!=null&&!cookieSet.isEmpty()){
            for (Cookie cookie:cookieSet){
                if (cookie.name().equals(name)) {
                    return cookie.value();
                }
            }
        }
        return null;
    }

    /**
     * @return the part of this request's URL from the protocol name up to the query string in the first line of the HTTP request.
     */
    public String uri() {
        // TODO: 2016/10/13 存疑 测试
        return this.queryStringDecoder.uri();
//        return fullHttpRequest.getRequestURI();
    }

    /**
     * @return Returns the name and version of the protocol the request uses
     */
    public String protocol() {
        return fullHttpRequest.protocolVersion().text();
    }

    private static Map<String, String> getParams(List<String> request, List<String> matched) {
//        LOG.debug("get params");

        Map<String, String> params = new HashMap<>();

        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (SparkUtils.isParam(matchedPart)) {
//                LOG.debug("matchedPart: "
//                                  + matchedPart
//                                  + " = "
//                                  + request.get(i));
                params.put(matchedPart.toLowerCase(), request.get(i));
            }
        }
        return Collections.unmodifiableMap(params);
    }

    private static List<String> getSplat(List<String> request, List<String> matched) {
//        LOG.debug("get splat");

        int nbrOfRequestParts = request.size();
        int nbrOfMatchedParts = matched.size();

        boolean sameLength = (nbrOfRequestParts == nbrOfMatchedParts);

        List<String> splat = new ArrayList<>();

        for (int i = 0; (i < nbrOfRequestParts) && (i < nbrOfMatchedParts); i++) {
            String matchedPart = matched.get(i);

            if (SparkUtils.isSplat(matchedPart)) {

                StringBuilder splatParam = new StringBuilder(request.get(i));
                if (!sameLength && (i == (nbrOfMatchedParts - 1))) {
                    for (int j = i + 1; j < nbrOfRequestParts; j++) {
                        splatParam.append("/");
                        splatParam.append(request.get(j));
                    }
                }
                splat.add(splatParam.toString());
            }
        }
        return Collections.unmodifiableList(splat);
    }

    /**
     * Set the session validity
     *
     * @param validSession the session validity
     */
    void validSession(boolean validSession) {
        this.validSession = validSession;
    }

}
