package com.bay1ts.bay.handler.intercepters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by chenu on 2016/10/17.
 */
public interface Interceptor {

    void onRequestReceived(ChannelHandlerContext ctx, HttpRequest e);

    void onRequestSuccessed(ChannelHandlerContext ctx, HttpRequest e,
                            HttpResponse response);

    void onRequestFailed(ChannelHandlerContext ctx, Throwable e,
                         HttpResponse response);
}
