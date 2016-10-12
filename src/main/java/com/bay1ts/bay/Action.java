package com.bay1ts.bay;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by chenu on 2016/9/3.
 */
public interface Action {
    public FullHttpResponse handle(FullHttpRequest request, FullHttpResponse response);
}
