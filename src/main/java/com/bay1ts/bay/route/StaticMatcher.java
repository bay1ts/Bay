package com.bay1ts.bay.route;

import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.*;
import com.bay1ts.bay.route.util.ClassPathResource;
import com.bay1ts.bay.utils.Assert;
import com.bay1ts.bay.utils.IOUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenu on 2016/10/14.
 */
// TODO: 2016/10/14 记着spring mvc吹过,好像请求结尾的 扩展名是啥,就按哪种方式解析..不知道怎么个意思
public class  StaticMatcher {
    private Logger logger= LoggerFactory.getLogger(StaticMatcher.class);
    private List<StaticRouteImpl> staticRoutes=new ArrayList<>();
    public boolean consume(ChannelHandlerContext ctx, FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        try {
            if (consumeWithFileResourceHandlers(ctx,httpRequest, httpResponse)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("requesting static file,but not found");
            return false;
        }
        return false;
    }
    public void path(String path){
        Assert.notNull(path,"folder must not be null");
        List<String> list=new ArrayList<>();
        // TODO: 2016/10/14 不完善 应该支持列表的 应该支持以逗号隔开
        //现在支持多个  静态目录了
        list.add(Config.instance().getWelcomeFile());
        this.staticRoutes.add(new StaticRouteImpl(path,list));
    }


    private boolean consumeWithFileResourceHandlers(ChannelHandlerContext ctx, FullHttpRequest httpRequest,
                                                    FullHttpResponse httpResponse) throws IOException {
        if (!staticRoutes.isEmpty()) {
            Request request=new Request(null,httpRequest);
            for (StaticRouteImpl staticRoute : staticRoutes) {
                //当然不会了下面的方法返回的resource是null,所以会空转一圈
                ClassPathResource resource = staticRoute.getResource(request);

                if (resource != null && resource.isReadable()) {
                    byte[] buf=IOUtils.toByteArray(resource.getInputStream());

                    boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
                    if (keepAlive) {
                        httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//                        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
                    }
                    httpResponse=httpResponse.replace(Unpooled.copiedBuffer(buf));
                    httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes())
                            .set(HttpHeaderNames.SERVER,"Bay1ts'Server YEE!");
                    ChannelFuture future = ctx.channel().writeAndFlush(httpResponse);
                    if (!keepAlive) {
                        future.addListener(ChannelFutureListener.CLOSE);
                    }

                    return true;
                }
            }

        }
        return false;
    }
}



















