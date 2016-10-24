package com.bay1ts.bay.route.match;

import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.HttpMethod;
import com.bay1ts.bay.core.Session;
import com.bay1ts.bay.core.session.HttpSessionThreadLocal;
import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DoRoute {
    private static Logger logger= LoggerFactory.getLogger(DoRoute.class);
    private static Gson gson=new Gson();
    public static void execute(RouteContext context) throws Exception {
        Object content = context.body().get();
        RouteMatch match = context.routeMatcher().find(context.httpMethod(), context.uri(), context.acceptType());

        Action action = null;
        if (match != null) {
            action = match.getAction();
        } else if (context.httpMethod() == HttpMethod.head && context.body().notSet()) {
            // See if get is mapped to provide default head mapping
            //并不知道这是在干吗
            content =
                    context.routeMatcher().find(HttpMethod.get, context.uri(), context.acceptType())
                            != null ? "" : null;
        }
        Object result = null;
        if (action != null) {
            //maybe bug
            context.withRequest(new Request(match,context.httpRequest()));
//            Request request=context.request();
//            // TODO: 2016/10/24 请注意下面.set_cookie并不是每次都调用的 只是在 本地找不到 id的时候执行
//            if (request.cookie("JSESSIONID")==null||!(HttpSessionThreadLocal.get()!=null&&request.cookie("JSESSIONID").equals(HttpSessionThreadLocal.get().getId()))){
//                System.out.println("这里是空 你可以把下一行粘贴到这里");
//            }
            //session部分请注意下面这行.调用了session方法 第二次 换了一个url请求的时候.没有从 cookie里获得 上次传给client的id,这里会重建

            result =action.handle(context.request(),context.response());
            if (result != null) {
                content = result;

                if (content instanceof String) {
                    String contentStr = (String) content;

                    if (!contentStr.equals("")) {
                        context.response().body(contentStr);
                    }
                }else if (content instanceof Object){
                    logger.debug("action returns an object,jsoning");
                    content=gson.toJson(content);
                    context.response().body(gson.toJson(content));
                }

            }
        }
        context.body().set(content);
    }

}




















