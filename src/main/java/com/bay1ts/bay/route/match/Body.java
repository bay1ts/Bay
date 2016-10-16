package com.bay1ts.bay.route.match;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.io.IOException;

public final class Body {

    private Object content;

    public static Body create() {
        return new Body();
    }

    private Body() {

    }

    public boolean notSet() {
        return content == null;
    }

    public boolean isSet() {
        return content != null;
    }

    public Object get() {
        return content;
    }

    public void set(Object content) {
        this.content = content;
    }

    public FullHttpResponse serializeTo(FullHttpResponse httpResponse,
                                        FullHttpRequest httpRequest) throws IOException {

        if ("".equals(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE) )|| httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE) == null)
        {
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html; charset=utf-8");
        }
        httpResponse=httpResponse.replace(Unpooled.copiedBuffer(((String)content).getBytes()));
        return httpResponse;
    }
}
