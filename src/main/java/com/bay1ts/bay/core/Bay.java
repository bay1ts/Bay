package com.bay1ts.bay.core;

import com.bay1ts.bay.Config;
import com.bay1ts.bay.core.session.BaseSessionStore;
import com.bay1ts.bay.core.session.MemoryBasedSessionStore;
import com.bay1ts.bay.core.session.RedisBasedSessionStore;
import com.bay1ts.bay.handler.MainHandler;
import com.bay1ts.bay.handler.intercepters.ChannelInterceptor;
import com.bay1ts.bay.handler.intercepters.SessionInterceptor;
import com.bay1ts.bay.route.RouteEntry;
import com.bay1ts.bay.route.TreeNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by chenu on 2016/10/12.
 * 这个类主要被 静态引入.作为此框架的入口
 */
public class Bay {
    private static Logger logger = LoggerFactory.getLogger(Bay.class);

    public static void listenAndStart() throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().
//                                    addLast(new ReadTimeoutHandler(5)).
        addLast("req_resp", new HttpServerCodec()).
                                    addLast("aggregator", new HttpObjectAggregator(65536)).
                                    //参看https://imququ.com/post/transfer-encoding-header-in-http.html
                                            addLast("deflater", new HttpContentCompressor(9)).
                                    //大文件支持
                                            addLast("streamer", new ChunkedWriteHandler()).
                                    //下面这个可以放到 前面 当 发生idle事件的时候,就会抛出异常,后面要有个 处理这种异常的handler,用来心跳.
                                    //参看 权威指南 私有协议的实现
                                            addLast("idlehandler", new IdleStateHandler(10, 30, 0)).
                                    addLast("mainHandler", getMainHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(Config.getPort()).sync();
            logger.info("Server started and listening on port " + Config.getPort());
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static MainHandler getMainHandler() {
        MainHandler handler = new MainHandler();
        handler
                .addInterceptor(new ChannelInterceptor())
                .addInterceptor(new SessionInterceptor(getHttpSessionStore()));


        return handler;
    }

    private static BaseSessionStore getHttpSessionStore() {
        BaseSessionStore sessionStore = Config.isEnableRedisSessionStore() ? new RedisBasedSessionStore() : new MemoryBasedSessionStore();
        // TODO: session过期处理
        new Thread(new Runnable() {
            boolean watchingSession = false;

            @Override
            public void run() {
                while (!watchingSession) {
                    try {
                        sessionStore.destroyInactiveSessions();
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error("something wrong with  destroy inactive session");
                        continue;
                    }
                }
            }
        }).start();
        return sessionStore;
    }

    private static Service getInstance() {
        return SingletonRouter.INSTANCE;
    }

    private static class SingletonRouter {
        private static final Service INSTANCE = new Service();
    }

    public static void staticResources(String floder) {
        getInstance().staticResources(floder);
    }

    public static void before(final String path,final Action action){
        getInstance().before(path,action);
    }

    //暴露给外面的接口.最基本的  restful 接口
    public static void get(final String path, final Action action) {
        getInstance().get(path, action);
    }

    public static void post(final String path, final Action action) {
        getInstance().post(path, action);
    }

    public static void put(final String path, final Action action) {
        getInstance().put(path, action);
    }

    public static void patch(final String path, final Action action) {
        getInstance().patch(path, action);
    }

    public static void delete(final String path, final Action action) {
        getInstance().delete(path, action);
    }

    public static void head(final String path, final Action action) {
        getInstance().head(path, action);
    }

    public static void trace(final String path, final Action action) {
        getInstance().trace(path, action);
    }

    public static void connect(final String path, final Action action) {
        getInstance().connect(path, action);
    }

    public static void options(final String path, final Action action) {
        getInstance().options(path, action);
    }
    // TODO: 2016/10/16 any

    public static TreeNode NSGet(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.get,path,action);
    }
    public static TreeNode NSPost(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.post,path,action);
    }
    public static TreeNode NSPut(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.put,path,action);
    }
    public static TreeNode NSPatch(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.patch,path,action);
    }
    public static TreeNode NSDelete(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.delete,path,action);
    }
    public static TreeNode NSHead(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.head,path,action);
    }
    public static TreeNode NSTrace(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.trace,path,action);
    }
    public static TreeNode NSConnect(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.connect,path,action);
    }
    public static TreeNode NSOptions(String path, Action action) {
        return getInstance().NSAdd(HttpMethod.options,path,action);
    }
    public static TreeNode NSBefore(String path,Action action){
        //not sure
        return getInstance().NSAdd(HttpMethod.before,path,action);
    }


    public static TreeNode newNameSpace(String path, TreeNode... routeEntries) {
        return getInstance().newNameSpace(path,routeEntries);
    }


    public static void NSRoute(TreeNode... treeNodes) {
        getInstance().NSRoute(treeNodes);
    }

}
