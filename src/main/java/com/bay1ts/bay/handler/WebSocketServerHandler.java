package com.bay1ts.bay.handler;

import com.bay1ts.bay.core.WebSocketAction;
import com.bay1ts.bay.core.WebSocketContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;

/**
 * Created by chenu on 2016/11/16.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private ChannelGroup channels = null;
    private final Map<String, WebSocketAction> webSocketRoutes;
    private WebSocketAction action;
    WebSocketContext webSocketContext = new WebSocketContext();

    public WebSocketServerHandler(Map<String, WebSocketAction> webSocketRoutes, ChannelGroup channels) {
        this.webSocketRoutes = webSocketRoutes;
        this.channels = channels;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        try {
            onCall(ctx, msg);
        } catch (Exception e) {
            System.out.println("read0");
        }

    }

    private void onCall(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws InterruptedException {
        webSocketContext.setTextWebSocketFrame(msg);
        action.onMessage(webSocketContext);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("add a channel " + ctx.channel().id() + " to channels");
        channels.add(ctx.channel());
        webSocketContext.setChannels(channels);
        webSocketContext.setChannelHandlerContext(ctx);
        System.out.println("-=-=-=");
        ctx.pipeline().forEach((a)->{
            System.out.println(a+" -");
        });
        CWebSocketServerProtocolHandshakeHandler handler=ctx.pipeline().get(CWebSocketServerProtocolHandshakeHandler.class);
        System.out.println(handler+" is null???");
        FullHttpRequest request=handler.getRequest();
        System.out.println(request+"is null!!!!????");
        String url = handler.getRequest().uri();
        System.out.println(url+"------");
        System.out.println("求log 正在为 ws请求 "+url+" 配置action");
        this.action = webSocketRoutes.get(url);
        System.out.println(action+"==============");
        this.action.onConnect(webSocketContext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        this.action.onClose(webSocketContext);
    }
}
