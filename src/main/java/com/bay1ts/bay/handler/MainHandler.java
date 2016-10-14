package com.bay1ts.bay.handler;
import com.bay1ts.bay.core.Bay;
import com.bay1ts.bay.core.Response;
import com.bay1ts.bay.route.HttpMethod;
import com.bay1ts.bay.core.Service;
import com.bay1ts.bay.route.Routes;
import com.bay1ts.bay.route.match.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import java.io.IOException;
import static com.bay1ts.bay.core.Bay.*;

/**
 * Created by chenu on 2016/8/15.
 */
public class MainHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
//    private Routes routeMatcher=getInstance().getRouterMatcher()
    private Routes routeMatcher= Service.getRouterMatcher();
    private String staticResources=Service.StaticResourcesLocation();


    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        this.onCall(ctx, request, response);
    }

    private void onCall(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, FullHttpResponse fullHttpResponse) {
        // TODO: 2016/10/12 静态资源的处理 参看 spark.http.matching.MatcherFilter 100行左右
//        System.out.println("-------------"+fullHttpRequest.method().name());

        if (staticFiles.consume(fullHttpRequest,fullHttpResponse)){
            return;
        }
        HttpMethod httpMethod = HttpMethod.valueOf(fullHttpRequest.method().name().toLowerCase());
        String uri = fullHttpRequest.uri();
        String acceptType = fullHttpRequest.headers().get(HttpHeaderNames.ACCEPT);

        // TODO: 2016/10/12 routecontext package spark.http.matching.MatcherFilter line 112
        RouteContext context = RouteContext.create();
        Response response=new Response(fullHttpResponse);
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
            // TODO: 2016/10/12 log and do something
        }
//        FullHttpResponse finalResponse = Service.getAction(request.uri()).handle(request,response);
//        boolean keepAlive= HttpUtil.isKeepAlive(request);
//        if (keepAlive){
//            finalResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//            finalResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.TEXT_PLAIN);
//        }
//
//        finalResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,finalResponse.content().readableBytes());
//
//        ChannelFuture future=ctx.channel().writeAndFlush(finalResponse);
//        if(!keepAlive){
//            future.addListener(ChannelFutureListener.CLOSE);
//        }
        // If redirected and content is null set to empty string to not throw NotConsumedException


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

//        if (body.notSet() && !externalContainer) {
//            LOG.info("The requested route [" + uri + "] has not been mapped in Spark");
//            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            body.set(String.format(NOT_FOUND));
//        }

        if (body.isSet()) {
            FullHttpResponse finalResponse = null;
            try {
                finalResponse = body.serializeTo(fullHttpResponse, fullHttpRequest);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 2016/10/13 log spark写的是向上级抛出了
            }

            boolean keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
            if (keepAlive) {
                finalResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                finalResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            }

            finalResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, finalResponse.content().readableBytes());

            ChannelFuture future = ctx.channel().writeAndFlush(finalResponse);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            // TODO: 2016/10/13 给response写个error吧
        }
    }


}
