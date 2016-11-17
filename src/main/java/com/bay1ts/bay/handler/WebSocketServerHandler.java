package com.bay1ts.bay.handler;

import com.bay1ts.bay.route.match.DoRoute;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chenu on 2016/11/16.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        onCall(ctx,msg);

    }

    private void onCall(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws InterruptedException {
        System.out.println("收到websocket消息");
        TextWebSocketFrame msg2=msg.copy();
        ctx.writeAndFlush(msg.retain());
        Thread.sleep(3000);
        ctx.writeAndFlush(new TextWebSocketFrame("不知道该做成什么样子,,,,"));
    }
}
