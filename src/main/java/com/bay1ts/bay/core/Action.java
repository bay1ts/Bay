package com.bay1ts.bay.core;

import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by chenu on 2016/9/3.
 */
public interface Action {
    Object handle(Request request,Response response) throws Exception;
}
