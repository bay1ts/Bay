package com.bay1ts.bay.route.match;

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

    // TODO: 2016/10/13 没什么理由 直觉告诉我这里的参数,1,3需要用 netty提供的 最后可能要返回 response 用来 writeand flush
    //第二个参数要重写那个方法
    public FullHttpResponse serializeTo(FullHttpResponse httpResponse,
                                        FullHttpRequest httpRequest) throws IOException {
        //说实话我觉着这个判断有点无聊,肯定没有返回response呢.程序员干嘛吃的这都不知道
//        if (!httpResponse.isCommitted()) {
        if ("".equals(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE) )|| httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE) == null)
        {
//            if (httpResponse.getContentType() == null) {
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html; charset=utf-8");
        }

        // Check if gzip is wanted/accepted and in that case handle that
//        OutputStream responseStream = GzipUtils.checkAndWrap(httpRequest, httpResponse, true);

        // serialize the body to output stream
        byte[] bytes=(byte[])content;
        // TODO: 2016/10/13 need help need test
        httpResponse.replace(Unpooled.copiedBuffer(bytes));
//        serializerChain.process(responseStream, content);

//        responseStream.flush(); // needed for GZIP stream. NOt sure where the HTTP response actually gets cleaned up
//        responseStream.close(); // needed for GZIP
//        }
        return httpResponse;
    }


}
