package com.bay1ts.bay.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Created by chenu on 2016/11/17.
 */
public class WebSocketContext {
    private ChannelHandlerContext channelHandlerContext;
    private TextWebSocketFrame textWebSocketFrame;

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public TextWebSocketFrame getTextWebSocketFrame() {
        return textWebSocketFrame;
    }

    public void setTextWebSocketFrame(TextWebSocketFrame textWebSocketFrame) {
        this.textWebSocketFrame = textWebSocketFrame;
    }
}
