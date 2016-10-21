package com.bay1ts.bay.route.match;

import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.HttpMethod;
import com.bay1ts.bay.core.Request;

import java.util.List;

/**
 * Created by chenu on 2016/10/13.
 */
public class BeforeFilters {
    public static void execute(RouteContext context) throws Exception {
        Object content = context.body().get();
        List<RouteMatch> matchSet = context.routeMatcher().findMultiple(HttpMethod.before, context.uri(), context.acceptType());

        for (RouteMatch filterMatch : matchSet) {
            Action filterTarget = filterMatch.getAction();
            Request request = new Request(filterMatch, context.httpRequest());
            context.withRequest(request);
            filterTarget.handle(context.request(), context.response());
            String bodyAfterFilter = context.response().body();
            if (bodyAfterFilter != null) {
                content = bodyAfterFilter;
            }


        }

        context.body().set(content);
    }
}
