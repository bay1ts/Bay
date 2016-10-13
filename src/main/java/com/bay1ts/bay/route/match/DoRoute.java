package com.bay1ts.bay.route.match;

import com.bay1ts.bay.Action;
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

        if (action != null) {
            Object result = null;
            //哎是我大意了.从此多事了
            //哎是我大意了.从此多事了
            //哎是我大意了.从此多事了
//            action.handle(context.)
        }

        context.body().set(content);
    }

}
