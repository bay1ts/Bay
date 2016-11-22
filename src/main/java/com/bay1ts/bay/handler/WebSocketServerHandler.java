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
        try {

            //新问题,如果有多个handler,怎么保证 能取到对的那个呢
            DWebSocketServerProtocolHandshakeHandler handler=ctx.pipeline().get(DWebSocketServerProtocolHandshakeHandler.class);
            ctx.pipeline().forEach((e)->{
                System.out.println(e+" --");
            });
            System.out.println(handler+" is handler null!!!!!????");
            String url = handler.getWebsocketPath();
            System.out.println("求log 正在为 ws请求 "+url+" 配置action");
            this.action = webSocketRoutes.get(url);
            webSocketContext.setTextWebSocketFrame(msg);
            action.onMessage(webSocketContext);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("add a channel " + ctx.channel().id() + " to channels");
        channels.add(ctx.channel());
        webSocketContext.setChannels(channels);
        webSocketContext.setChannelHandlerContext(ctx);
//        this.action.onConnect(webSocketContext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        this.action.onClose(webSocketContext);
    }
}
