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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Created by chenu on 2016/11/16.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    boolean read =false;
    private Logger logger= LoggerFactory.getLogger(WebSocketServerHandler.class);
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
        if (!read){
            onIn(ctx);
            read=true;
        }
        //可以把context的初始化,放在in方法中,然后在这个位置上可以 做个连接 的回调(假装)
        //有小点点局限,就是发过一次消息才算 连接上了
        onCall(ctx, msg);

    }

    private void onIn(ChannelHandlerContext ctx) {
        String url = ctx.channel().attr(PATH).get();
        logger.info("从 "+ctx.channel().id()+" channel中得到 url "+ url);
        this.action = webSocketRoutes.get(url);
        logger.info("exec before onMessage");
        ChannelGroup channelGroup=null;
        if (pathChannels.containsKey(url)){
            logger.info("path "+url+" requested by others channel");
            channelGroup=pathChannels.get(url);
        }else {
            logger.info("this is the first time url "+url+" was requested by any channel");
            channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            pathChannels.put(url,channelGroup);
        }
        logger.info("adding channel "+ctx.channel()+" to channelGroup "+url);
        channelGroup.add(ctx.channel());
        //该检查检查map里都有些什么
        pathChannels.forEach((path,channelGroupTmp)->{
            logger.info("url "+path+" contains channels : "+channelGroupTmp.size());
        });
        webSocketContext.setChannels(pathChannels.get(url));
        webSocketContext.setChannelHandlerContext(ctx);

        action.onConnect(webSocketContext);
    }

    private void onCall(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws InterruptedException {
        try {
            webSocketContext.setTextWebSocketFrame(msg);
            logger.info("calling callback method onMessage");
            action.onMessage(webSocketContext);
        } catch (Exception e) {
            // TODO: 2016/11/25 add onError callback there
            action.onError(webSocketContext);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel "+ctx.channel().id()+" inactive,calling action.onClose callback");
        this.action.onClose(webSocketContext);
    }
}
