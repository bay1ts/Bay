package com.bay1ts.bay.route;

import com.bay1ts.bay.Config;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.utils.Assert;
import com.bay1ts.bay.utils.IOUtils;
import com.sun.xml.internal.bind.v2.TODO;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenu on 2016/10/14.
 */
public class  StaticMatcher {
    private List<StaticRouteImpl> staticRoutes;
    public boolean consume(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        try {
            if (consumeWithFileResourceHandlers(httpRequest, httpResponse)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 2016/10/14 log
            return false;
        }
        return false;
    }
    public void path(String path){
        Assert.notNull(path,"folder must not be null");
        List<String> list=new ArrayList<>();
        // TODO: 2016/10/14 不完善 应该支持列表的 应该支持以逗号隔开
        //现在支持多个  静态目录了
        list.add(Config.welcomeFile);
        this.staticRoutes.add(new StaticRouteImpl(path,list));
    }


    private boolean consumeWithFileResourceHandlers(FullHttpRequest httpRequest,
                                                    FullHttpResponse httpResponse) throws IOException {
        if (!staticRoutes.isEmpty()) {
            Request request=new Request(null,httpRequest);
            for (StaticRouteImpl staticRoute : staticRoutes) {

                ClassPathResource resource = staticRoute.getResource(request);

                if (resource != null && resource.isReadable()) {
//                    OutputStream wrappedOutputStream = GzipUtils.checkAndWrap(httpRequest, httpResponse, false);
//                    customHeaders.forEach(httpResponse::setHeader); //add all user-defined headers to response
//                    IOUtils.copy(resource.getInputStream(), wrappedOutputStream);
//                    wrappedOutputStream.flush();
//                    wrappedOutputStream.close();
                    // TODO: 2016/10/14 将resource放到response里面去.顺便把response的header处理干净
                    return true;
                }
            }

        }
        return false;
    }
}



















