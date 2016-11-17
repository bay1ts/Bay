package com.bay1ts.bay.handler;

import com.bay1ts.bay.core.WebSocketAction;
import com.bay1ts.bay.core.WebSocketContext;
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

    private final WebSocketAction webSocketAction;

    public WebSocketServerHandler(WebSocketAction webSocketAction) {
        this.webSocketAction=webSocketAction;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        onCall(ctx,msg);
    }

    private void onCall(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws InterruptedException {
        WebSocketContext webSocketContext=new WebSocketContext();
        webSocketContext.setChannelHandlerContext(ctx);
        webSocketContext.setTextWebSocketFrame(msg);
        this.webSocketAction.handle(webSocketContext);
    }
}
