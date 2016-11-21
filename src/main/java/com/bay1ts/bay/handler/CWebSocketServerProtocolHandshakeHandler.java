package com.bay1ts.bay.handler;

import com.bay1ts.bay.core.WebSocketAction;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;

import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by chenu on 2016/11/21.
 */
public class CWebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {
    private final Map<String,WebSocketAction> webSocketRoutes=null;
    private final String websocketPath;
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadSize;
    private final boolean allowMaskMismatch;

    CWebSocketServerProtocolHandshakeHandler(String websocketPath, String subprotocols,
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
        if (!webSocketRoutes.keySet().contains(req.uri())){
            ctx.fireChannelRead(msg);
            return;
        }

        //这里用循环判断 所有path都不在这里,才传到下一个handler
//        if (!websocketPath.equals(req.uri())) {
//            ctx.fireChannelRead(msg);
//            return;
//        }

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
                CWebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handshaker);
                ctx.pipeline().replace(this, "WS403Responder",
                        CWebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
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
