package com.bay1ts.bay.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

/**
 * Created by chenu on 2016/11/17.
 */
public class WebSocketContext {
    private ChannelHandlerContext channelHandlerContext;
    private TextWebSocketFrame textWebSocketFrame;
    private ChannelGroup channels;
    private static final AttributeKey<String> PATH = AttributeKey.valueOf("PATH");

    public void broadcast(String message){
        for (Channel channel:channels){
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }

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

    public ChannelGroup getChannels() {
        return channels;
    }

    public void setChannels(ChannelGroup channels) {
        this.channels = channels;
    }
}
