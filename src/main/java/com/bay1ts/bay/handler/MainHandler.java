package com.bay1ts.bay.handler;

import com.bay1ts.bay.route.Routes;
import com.bay1ts.bay.route.match.*;
import com.bay1ts.bay.route.HttpMethod;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by chenu on 2016/8/15.
 */
public class MainHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Routes routeMatcher;


    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,Unpooled.copiedBuffer("wow welcome to Bay1ts' web framework based on netty", CharsetUtil.UTF_8));
        this.onCall(ctx,request,response);
    }

    private void onCall(ChannelHandlerContext ctx,FullHttpRequest fullHttpRequest, FullHttpResponse fullHttpResponse) {
        // TODO: 2016/10/12 静态资源的处理 参看 spark.http.matching.MatcherFilter 100行左右
        HttpMethod httpMethod= HttpMethod.valueOf(fullHttpRequest.method().name());
        String uri=fullHttpRequest.uri();
        String acceptType=fullHttpRequest.headers().get(HttpHeaderNames.ACCEPT);

        // TODO: 2016/10/12 routecontext package spark.http.matching.MatcherFilter line 112
        RouteContext context=RouteContext.create();
        Body body=Body.create();
        context
                .withMatcher(routeMatcher)
                .withHttpRequest(fullHttpRequest)
                .withUri(uri)
                .withAcceptType(acceptType)
                .withBody(body)
                .withResponse(fullHttpResponse)
                .withHttpMethod(httpMethod);
        try {
            BeforeFilters.execute(context);
            DoRoute.execute(context);
            AfterFilters.execute(context);
        }catch (Exception e){
            // TODO: 2016/10/12 log and do something
        }
//        FullHttpResponse finalResponse = Router.getAction(request.uri()).handle(request,response);
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


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
