package com.bay1ts.bay.handler;

import com.bay1ts.bay.core.WebSocketAction;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.*;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by chenu on 2016/11/21.
 */
public class CWebSocketServerProtocolHandler extends WebSocketServerProtocolHandler {
    private final Map<String,WebSocketAction> webSocketRoutes;
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadLength;
    private final boolean allowMaskMismatch;
    public CWebSocketServerProtocolHandler(Map<String,WebSocketAction> webSocketRoutes) {
        this(webSocketRoutes, null, false);
    }

    public CWebSocketServerProtocolHandler(Map<String,WebSocketAction> webSocketRoutes, String subprotocols) {
        this(webSocketRoutes, subprotocols, false);
    }

    public CWebSocketServerProtocolHandler(Map<String,WebSocketAction> webSocketRoutes, String subprotocols, boolean allowExtensions) {
        this(webSocketRoutes, subprotocols, allowExtensions, 65536);
    }

    public CWebSocketServerProtocolHandler(Map<String,WebSocketAction> webSocketRoutes, String subprotocols, boolean allowExtensions, int maxFrameSize) {
        this(webSocketRoutes, subprotocols, allowExtensions, maxFrameSize, false);
    }

    public CWebSocketServerProtocolHandler(Map<String,WebSocketAction> webSocketRoutes, String subprotocols, boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
        super(null, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch);
        this.webSocketRoutes = webSocketRoutes;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        maxFramePayloadLength = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(CWebSocketServerProtocolHandshakeHandler.class) == null) {
            // Add the WebSocketHandshakeHandler before this one.
            ctx.pipeline().addBefore(ctx.name(), CWebSocketServerProtocolHandshakeHandler.class.getName(),
                    new CWebSocketServerProtocolHandshakeHandler(webSocketRoutes, subprotocols,
                            allowExtensions, maxFramePayloadLength, allowMaskMismatch));
        }
        if (cp.get(Utf8FrameValidator.class) == null) {
            // Add the UFT8 checking before this one.
            ctx.pipeline().addBefore(ctx.name(), Utf8FrameValidator.class.getName(),
                    new Utf8FrameValidator());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        super.decode(ctx, frame, out);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof WebSocketHandshakeException) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.close();
        }
    }
}
