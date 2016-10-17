package com.bay1ts.bay.handler.intercepters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by chenu on 2016/10/17.
 */
public class SessionInterceptor implements Interceptor {
    @Override
    public void onRequestReceived(ChannelHandlerContext ctx, HttpRequest e) {

    }

    @Override
    public void onRequestSuccessed(ChannelHandlerContext ctx, HttpRequest e, HttpResponse response) {

    }

    @Override
    public void onRequestFailed(ChannelHandlerContext ctx, Throwable e, HttpResponse response) {

    }
}
