package com.bay1ts.bay.handler;

import com.bay1ts.bay.core.WebSocketAction;
import com.bay1ts.bay.core.WebSocketContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenu on 2016/11/16.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private Map<String, ChannelGroup> pathChannels = null;
    private final Map<String, WebSocketAction> webSocketRoutes;
    private WebSocketAction action;
    WebSocketContext webSocketContext = new WebSocketContext();
    private static final AttributeKey<String> PATH = AttributeKey.valueOf("PATH");

    public WebSocketServerHandler(Map<String, WebSocketAction> webSocketRoutes, Map<String, ChannelGroup> channels) {
        this.webSocketRoutes = webSocketRoutes;
        this.pathChannels = channels;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        onIn(ctx);
        //可以把context的初始化,放在in方法中,然后在这个位置上可以 做个连接 的回调(假装)
        //有小点点局限,就是发过一次消息才算 连接上了
        onCall(ctx, msg);

    }

    private void onIn(ChannelHandlerContext ctx) {
        System.out.println("业务handler 激活了");
        String url = ctx.channel().attr(PATH).get();
        System.out.println("业务handler 激活,从 "+ctx.channel().id()+" channel中拿出来了 url "+ url);
        ChannelGroup channelGroup=null;
        if (pathChannels.containsKey(url)){
            System.out.println("if "+ url+" ================");
            channelGroup=pathChannels.get(url);
        }else {
            System.out.println("else "+ url+" ================");
            channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            pathChannels.put(url,channelGroup);
        }
        channelGroup.add(ctx.channel());
        //该检查检查map里都有些什么
        pathChannels.forEach((a,b)->{
            System.out.println("url "+a+" contains channels : "+b.size());
        });
        webSocketContext.setChannels(pathChannels.get(url));
        webSocketContext.setChannelHandlerContext(ctx);
    }

    private void onCall(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws InterruptedException {
        try {
            String url = ctx.channel().attr(PATH).get();
            System.out.println("求log 正在为 ws请求 " + url + " 配置action");
            this.action = webSocketRoutes.get(url);
            webSocketContext.setTextWebSocketFrame(msg);
            action.onMessage(webSocketContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        this.action.onClose(webSocketContext);
    }

}
