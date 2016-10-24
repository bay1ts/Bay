package com.bay1ts.bay.route.match;

import com.bay1ts.bay.core.session.HttpSessionImpl;
import com.bay1ts.bay.core.session.HttpSessionThreadLocal;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * Created by chenu on 2016/10/13.
 */
public class AfterFilters {
    public static void execute(RouteContext context) throws Exception {
        // TODO: 2016/10/13
        String requestSessionID=context.request().cookie(HttpSessionImpl.SESSION_ID_KEY);
        if (requestSessionID!=null&& HttpSessionThreadLocal.getSessionStore().findSession(requestSessionID)!=null){
            return;
        }
        context.response().header(HttpHeaderNames.SET_COOKIE.toString(),"JSESSIONID="+context.request().session().id()+";Path=/");
    }
}
