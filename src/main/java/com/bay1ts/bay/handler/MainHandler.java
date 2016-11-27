package com.bay1ts.bay.handler;
import com.bay1ts.bay.core.*;
import com.bay1ts.bay.core.HttpMethod;
import com.bay1ts.bay.handler.intercepters.Interceptor;
import com.bay1ts.bay.route.Routes;
import com.bay1ts.bay.route.StaticMatcher;
import com.bay1ts.bay.route.filter.AfterFilters;
import com.bay1ts.bay.route.filter.BeforeFilters;
import com.bay1ts.bay.route.match.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by chenu on 2016/8/15.
 */
public class MainHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Logger logger= LoggerFactory.getLogger(MainHandler.class);
    private Routes routeMatcher= Service.getRouterMatcher();
    private StaticMatcher staticMatcher=Service.staticMatcher();
    private List<Interceptor> interceptors;
    private static final String SERVER_NAME="Bay1ts'Server YEE!!!";

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
            ctx.channel().writeAndFlush(new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE));
        }
        //handle static resource request
        if (staticMatcher.consume(ctx,fullHttpRequest,fullHttpResponse)){
            return;
        }

        for (Interceptor interceptor:interceptors){
            interceptor.onRequestReceived(ctx,fullHttpRequest);
        }

        //refer to servletbridgehandler.java line283

        //handle dynamic request
        HttpMethod httpMethod = HttpMethod.valueOf(fullHttpRequest.method().name().toLowerCase());
        String acceptType = fullHttpRequest.headers().get(HttpHeaderNames.ACCEPT);
        Response response=new Response(fullHttpResponse);
        // TODO: 2016/10/12 routecontext package spark.http.matching.MatcherFilter line 112
        RouteContext context = RouteContext.create();
        Body body = Body.create();
        Request request=new Request(null,fullHttpRequest);
        String uri =request.pathInfo();

        context
                .withMatcher(routeMatcher)
                .withHttpRequest(fullHttpRequest)
                .withUri(uri)
                .withAcceptType(acceptType)
                .withBody(body)
                .withResponse(response)
                .withHttpMethod(httpMethod)
                .withRequest(request);
        try {
            BeforeFilters.execute(context);
            DoRoute.execute(context);
            AfterFilters.execute(context);
        } catch (HaltException halt){
            context.response().status(halt.statusCode());
            if (halt.body() != null) {
                body.set(halt.body());
            } else {
                body.set("");
            }
        } catch (Exception e) {
            logger.error("something wrong on beforeFilters/doRoute/afterFilters execute");
            sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
            // TODO: 2016/10/16 500
        }
        if (body.notSet() && context.response().isRedirected()) {
            body.set("");
        }
        if (body.notSet() ) {
            logger.info("The requested route [" + uri + "] has not been mapped");
            fullHttpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            body.set(String.format("<html><body><h2>404 Not found</h2></body></html>"));
        }

        if (body.isSet()) {
            FullHttpResponse finalResponse = null;
            try {
                finalResponse = body.serializeTo(fullHttpResponse, fullHttpRequest);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("can't create response content!!!");
                sendError(ctx,INTERNAL_SERVER_ERROR);
            }
            boolean keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
            if (keepAlive) {
                finalResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            finalResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, finalResponse.content().readableBytes());
            finalResponse.headers().set(HttpHeaderNames.SERVER,SERVER_NAME);
            ChannelFuture future = ctx.channel().writeAndFlush(finalResponse);
            for (Interceptor interceptor:interceptors){
                interceptor.onRequestSuccessed(ctx,fullHttpRequest,fullHttpResponse);
            }
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            logger.error("null response content");
            sendError(ctx,NO_CONTENT);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Channel ch = ctx.channel();
        if (cause instanceof IllegalArgumentException) {
            ch.close();
        } else {
            if (cause instanceof TooLongFrameException) {
                sendError(ctx, BAD_REQUEST);
                return;
            }

            if (ch.isActive()) {
                sendError(ctx, INTERNAL_SERVER_ERROR);
            }

        }
    }
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        String text = "Failure: " + status.toString() + "\r\n";
        ByteBuf byteBuf = Unpooled.buffer();
        byte[] bytes = null;
        try {
            bytes = text.getBytes("utf-8");
            byteBuf.writeBytes(bytes);
        } catch (UnsupportedEncodingException e) {
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, byteBuf);
        HttpHeaders headers = response.headers();

        headers.add(CONTENT_TYPE, "text/plain;charset=utf-8");
        headers.add(CACHE_CONTROL, "no-cache");
        headers.add(PRAGMA, "No-cache");
        headers.add(SERVER, SERVER_NAME);
        headers.add(CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
