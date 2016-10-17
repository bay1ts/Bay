package com.bay1ts.bay.handler.intercepters;

import com.bay1ts.bay.core.ChannelThreadLocal;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;

/**
 * Created by chenu on 2016/10/17.
 */
public class ChannelInterceptor implements Interceptor {
    @Override
    public void onRequestReceived(ChannelHandlerContext ctx, HttpRequest e) {
        ChannelThreadLocal.set(ctx.channel());
    }

    @Override
    public void onRequestSuccessed(ChannelHandlerContext ctx, HttpRequest e, HttpResponse response) {
        // TODO: 2016/10/17 存疑,如果是  http1,1的请求,结束后不会关闭这个channel.so.一成功就unset可能会出现对channel的引用的空指针呢
        if (HttpUtil.isKeepAlive(e)){
            return;
        }
        ChannelThreadLocal.unset();
    }

    @Override
    public void onRequestFailed(ChannelHandlerContext ctx, Throwable e, HttpResponse response) {
        ChannelThreadLocal.unset();
    }
}
