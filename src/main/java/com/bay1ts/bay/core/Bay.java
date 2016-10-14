package com.bay1ts.bay.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import com.bay1ts.bay.handler.MainHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by chenu on 2016/10/12.
 * 这个类主要被 静态引入.作为此框架的入口
 */
public class Bay {
    public static void listenAndStart(int port) throws Exception {
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
                                    addLast(new HttpServerCodec()).
                                    addLast("aggregator",new HttpObjectAggregator(65536)).
                                    //大文件支持
                                    addLast("streamer",new ChunkedWriteHandler()).
                                    addLast("mainHandler",new MainHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG,1024).childOption(ChannelOption.SO_KEEPALIVE,true);
//                    .childHandler(new ChildChannelHandler());
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    private static Service getInstance(){
        return SingletonRouter.INSTANCE;
    }
    private static class SingletonRouter{
        private static final Service INSTANCE=new Service();
    }

    public static void staticResources(String floder){
        getInstance().staticResources(floder);
    }


//暴露给外面的接口.最基本的  restful 接口
    public static void get(final String path, final Action action){
        getInstance().get(path,action);
    }
    public static void post(final String path, final Action action){
        getInstance().post(path,action);
    }
    public static void put(final String path, final Action action){
        getInstance().put(path,action);
    }
    public static void patch(final String path, final Action action){
        getInstance().patch(path,action);
    }
    public static void delete(final String path, final Action action){
        getInstance().delete(path,action);
    }
    public static void head(final String path, final Action action){
        getInstance().head(path,action);
    }
    public static void trace(final String path, final Action action){
        getInstance().trace(path,action);
    }
    public static void connect(final String path, final Action action){
        getInstance().connect(path,action);
    }
    public static void options(final String path, final Action action){
        getInstance().options(path,action);
    }

}
