package com.bay1ts.bay.route.match;

import com.bay1ts.bay.Action;
import com.bay1ts.bay.route.HttpMethod;
import com.bay1ts.bay.route.RouteImpl;

final class Routes {

    static void execute(RouteContext context) throws Exception {

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

        if (action != null) {
            Object result = null;
            //哎是我大意了.从此多事了
            //哎是我大意了.从此多事了
            //哎是我大意了.从此多事了

            RouteImpl route = ((RouteImpl) target);

            if (context.requestWrapper().getDelegate() == null) {
                Request request = RequestResponseFactory.create(match, context.httpRequest());
                context.requestWrapper().setDelegate(request);
            } else {
                context.requestWrapper().changeMatch(match);
            }

            context.responseWrapper().setDelegate(context.response());
            //请注意 是这里调用的
            Object element = route.handle(context.requestWrapper(), context.responseWrapper());
            result = route.render(element);


            if (result != null) {
                content = result;

                if (content instanceof String) {
                    String contentStr = (String) content;

                    if (!contentStr.equals("")) {
                        context.responseWrapper().body(contentStr);
                    }
                }
            }
        }

        context.body().set(content);
    }

}
