package com.bay1ts.bay.route.match;

import com.bay1ts.bay.Action;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.route.HttpMethod;

public final class DoRoute {

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

            //哎是我大意了.从此多事了
            //哎是我大意了.从此多事了
            //哎是我大意了.从此多事了
//            package spark.http.matching.Routes.java line 56
            //request.changeMatch(match)
            //按照原来那个是 要写下面这行(context里面现在没有getRequest方法) 虽然说我没看懂
            //context.getRequest().changeMatch(match)

            //2016年10月13日17:02:53
            //存疑 package spark.http.matching.Routes.java line 61
            //context的requestwarpper明明没什么东西啊,response没啥东西很正常.
            context.withRequest(new Request(match,context.httpRequest()));
            result =action.handle(context.request(),context.response());
            if (result != null) {
                content = result;

                if (content instanceof String) {
                    String contentStr = (String) content;

                    if (!contentStr.equals("")) {
                        context.response().body(contentStr);
                    }
                }
                //// TODO: 2016/10/13 目测上面 只支持string啊,并不能像springmvc那样能返回个object啊.
                //要是洋气点.就价格判断 ,如果是...(草,没法判断是哪个类.这块是个坑)  就在这里json一下成字符串
            }
        }
        context.body().set(content);
    }

}




















