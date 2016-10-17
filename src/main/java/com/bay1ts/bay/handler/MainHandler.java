package com.bay1ts.bay.handler;
import com.bay1ts.bay.core.ChannelThreadLocal;
import com.bay1ts.bay.core.Response;
import com.bay1ts.bay.core.HttpMethod;
import com.bay1ts.bay.core.Service;
import com.bay1ts.bay.core.session.HttpSessionImpl;
import com.bay1ts.bay.core.session.HttpSessionThreadLocal;
import com.bay1ts.bay.handler.intercepters.Interceptor;
import com.bay1ts.bay.route.Routes;
import com.bay1ts.bay.route.StaticMatcher;
import com.bay1ts.bay.route.match.*;
import com.bay1ts.bay.utils.Utils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.handler.codec.http.cookie.Cookie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by chenu on 2016/8/15.
 */
public class MainHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Logger logger= LoggerFactory.getLogger(MainHandler.class);
    private Routes routeMatcher= Service.getRouterMatcher();
    private StaticMatcher staticMatcher=Service.staticMatcher();
    private List<Interceptor> interceptors;

    public MainHandler addInterceptor(Interceptor interceptor){
        if (interceptors==null){
            interceptors=new ArrayList<>();
        }
        interceptors.add(interceptor);
        return this;
    }


    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uriPrefix="/";
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        if (request.uri().startsWith(uriPrefix)){
            this.onCall(ctx, request, response);
        }else {
            ctx.fireChannelRead(request);
        }

    }

    private void onCall(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, FullHttpResponse fullHttpResponse) {
        if (HttpUtil.is100ContinueExpected(fullHttpRequest)){
            ctx.channel().write(new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE));
        }
        //handle static resource request
        if (staticMatcher.consume(ctx,fullHttpRequest,fullHttpResponse)){
            return;
        }
        //handle dynamic request
        //from here
        ChannelThreadLocal.set(ctx.channel());
        HttpSessionThreadLocal.unset();
        Collection<Cookie> cookies = Utils.getCookies(
                HttpSessionImpl.SESSION_ID_KEY, fullHttpRequest);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String jsessionId = cookie.value();
                HttpSessionImpl s = HttpSessionThreadLocal.getSessionStore()
                        .findSession(jsessionId);
                if (s != null) {
                    HttpSessionThreadLocal.set(s);
                    break;
                }
            }
        }

        //to here needed to refactor
        //refer to servletbridgehandler.java line283
        HttpMethod httpMethod = HttpMethod.valueOf(fullHttpRequest.method().name().toLowerCase());
        String uri = fullHttpRequest.uri();
        String acceptType = fullHttpRequest.headers().get(HttpHeaderNames.ACCEPT);
        Response response=new Response(fullHttpResponse);
        // TODO: 2016/10/12 routecontext package spark.http.matching.MatcherFilter line 112
        RouteContext context = RouteContext.create();
        Body body = Body.create();
//        routeMatcher=new Routes();
        context
                .withMatcher(routeMatcher)
                .withHttpRequest(fullHttpRequest)
                .withUri(uri)
                .withAcceptType(acceptType)
                .withBody(body)
                .withResponse(response)
                .withHttpMethod(httpMethod);
        try {
            BeforeFilters.execute(context);
            DoRoute.execute(context);
            AfterFilters.execute(context);
        } catch (Exception e) {
            logger.error("something wrong on beforeFilters/doRoute/afterFilters execute");
            // TODO: 2016/10/16 500
        }

        /**
         * 这里按下不表 先搞上面的exec
         */
        if (body.notSet() && context.response().isRedirected()) {
            body.set("");
        }
        //不至于不至于
//        if (body.notSet() && hasOtherHandlers) {
//            if (servletRequest instanceof HttpRequestWrapper) {
//                ((HttpRequestWrapper) servletRequest).notConsumed(true);
//                return;
//            }
//        }

        if (body.notSet() ) {
            logger.info("The requested route [" + uri + "] has not been mapped");
            fullHttpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            body.set(String.format("<html><body><h2>404 Not found</h2></body></html>"));
        }

        if (body.isSet()) {
            FullHttpResponse finalResponse = null;
            try {
                // TODO: 2016/10/15 存疑 serializeTo 第一个参数应该是哪个responsese的问题
                finalResponse = body.serializeTo(fullHttpResponse, fullHttpRequest);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("can't create response content!!!");
            }
            boolean keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
            if (keepAlive) {
                finalResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            finalResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, finalResponse.content().readableBytes());
            finalResponse.headers().set(HttpHeaderNames.SERVER,"Bay1ts'Server YEE!!!");
            ChannelFuture future = ctx.channel().writeAndFlush(finalResponse);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            logger.error("null response content");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelThreadLocal.unset();
        super.exceptionCaught(ctx, cause);
    }
}
