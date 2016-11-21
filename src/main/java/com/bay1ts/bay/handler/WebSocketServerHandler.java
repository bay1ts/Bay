package com.bay1ts.bay.handler;

import com.bay1ts.bay.core.WebSocketAction;
import com.bay1ts.bay.core.WebSocketContext;
import com.bay1ts.bay.route.match.DoRoute;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by chenu on 2016/11/16.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private ChannelGroup channels=null;
    private final Map<String,WebSocketAction> webSocketRoutes;
    WebSocketContext webSocketContext=new WebSocketContext();
    public WebSocketServerHandler(Map<String, WebSocketAction> webSocketRoutes, ChannelGroup channels) {
        this.webSocketRoutes=webSocketRoutes;
        this.channels=channels;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        onCall(ctx,msg);
    }

    private void onCall(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws InterruptedException {
        try {
            System.out.println(((FullHttpRequest)msg).uri());
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        webSocketContext.setTextWebSocketFrame(msg);
        //现在唯一的问题 就是怎么把   path传送到这里来
//        this.webSocketAction.onMessage(webSocketContext);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("add a channel "+ctx.channel().id()+" to channels");
        channels.add(ctx.channel());
        webSocketContext.setChannels(channels);
        webSocketContext.setChannelHandlerContext(ctx);
//        this.webSocketAction.onConnect(webSocketContext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
//        this.webSocketAction.onClose(webSocketContext);
    }
}
