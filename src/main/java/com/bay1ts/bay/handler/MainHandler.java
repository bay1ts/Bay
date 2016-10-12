package com.bay1ts.bay.handler;

import com.bay1ts.bay.route.Router;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by chenu on 2016/8/15.
 */
public class MainHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,Unpooled.copiedBuffer("wow welcome to Bay1ts' web framework based on netty", CharsetUtil.UTF_8));
        this.doAction(ctx,request,response);
    }

    private void doAction(ChannelHandlerContext ctx,FullHttpRequest request, FullHttpResponse response) {
        request.method();
        FullHttpResponse finalResponse = Router.getAction(request.uri()).handle(request,response);
        boolean keepAlive= HttpUtil.isKeepAlive(request);
        if (keepAlive){
            finalResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            finalResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.TEXT_PLAIN);
        }

        finalResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,finalResponse.content().readableBytes());

        ChannelFuture future=ctx.channel().writeAndFlush(finalResponse);
        if(!keepAlive){
            future.addListener(ChannelFutureListener.CLOSE);
        }


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
