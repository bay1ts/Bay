package com.bay1ts.bay.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
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

//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().attr(PATH).set(this.websocketPath);
//        System.out.println("请注意啊请注意 handshaker已经添加了,");
//        System.out.println("DWebSocketServerProtocolHandshakeHandler line 36______channelRegistered,adding path "+websocketPath+"  to "+ctx.channel().id());
//    }

//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().attr(PATH).set(this.websocketPath);
//        System.out.println("请注意啊请注意 handshaker已经注册了,看看业务handler是否已经注册了");
//        System.out.println("DWebSocketServerProtocolHandshakeHandler line 36______channelRegistered,adding path "+websocketPath+"  to "+ctx.channel().id());
//    }


    private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY =
            AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
    private static final AttributeKey<String> PATH=AttributeKey.valueOf("PATH");

    public DWebSocketServerProtocolHandshakeHandler(String websocketPath, String subprotocols,
            boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch) {
        this.websocketPath = websocketPath;
        this.subprotocols = subprotocols;
        this.allowExtensions = allowExtensions;
        maxFramePayloadSize = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof HttpRequest)){
            //测试 before 或者parrent方法
            ctx.fireChannelRead(msg);
            return;
        }
        System.out.println("this is handshaker for "+websocketPath);
        FullHttpRequest req = (FullHttpRequest) msg;
        if (!websocketPath.equals(req.uri())) {
            ctx.fireChannelRead(msg);
            return;
        }
        System.out.println("注意啦各位,握手已经读到东西啦,现在就在往 channel "+ctx.channel().id()+" 写入path");
        ctx.channel().attr(PATH).set(this.websocketPath);
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
//                ctx.pipeline().replace(this, "WS403Responder",
//                        new ChannelInboundHandlerAdapter() {
//                            @Override
//                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                if (msg instanceof FullHttpRequest) {
//                                    ((FullHttpRequest) msg).release();
//                                    FullHttpResponse response =
//                                            new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FORBIDDEN);
//                                    ctx.channel().writeAndFlush(response);
//                                } else {
//                                    ctx.fireChannelRead(msg);
//                                }
//                            }
//                        });
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
