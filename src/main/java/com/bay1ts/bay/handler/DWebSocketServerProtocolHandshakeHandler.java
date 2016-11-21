package com.bay1ts.bay.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;

import static io.netty.handler.codec.http.HttpUtil.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class DWebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {

    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadSize;
    private final boolean allowMaskMismatch;
    private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY =
            AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");

    DWebSocketServerProtocolHandshakeHandler(String websocketPath, String subprotocols,
            boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
        this.websocketPath = websocketPath;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        maxFramePayloadSize = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        if (!websocketPath.equals(req.uri())) {
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if (req.method() != GET) {
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
                return;
            }

            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    getWebSocketLocation(ctx.pipeline(), req, websocketPath), subprotocols,
                            allowExtensions, maxFramePayloadSize, allowMaskMismatch);
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            ctx.fireUserEventTriggered(
                                    WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                        }
                    }
                });
                ctx.channel().attr(HANDSHAKER_ATTR_KEY).set(handshaker);
                ctx.pipeline().replace(this, "WS403Responder",
                        new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof FullHttpRequest) {
                                    ((FullHttpRequest) msg).release();
                                    FullHttpResponse response =
                                            new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FORBIDDEN);
                                    ctx.channel().writeAndFlush(response);
                                } else {
                                    ctx.fireChannelRead(msg);
                                }
                            }
                        });
            }
        } finally {
            req.release();
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            // SSL in use so use Secure WebSockets
            protocol = "wss";
        }
        return protocol + "://" + req.headers().get(HttpHeaderNames.HOST) + path;
    }
}
