package com.bay1ts.bay.handler.intercepters;

import com.bay1ts.bay.core.session.BaseSessionStore;
import com.bay1ts.bay.core.session.HttpSessionImpl;
import com.bay1ts.bay.core.session.HttpSessionThreadLocal;
import com.bay1ts.bay.utils.Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Collection;

import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

/**
 * Created by chenu on 2016/10/17.
 */
public class SessionInterceptor implements Interceptor {
    private boolean sessionRequestedByCookie = false;

    public SessionInterceptor(BaseSessionStore sessionStore) {
        HttpSessionThreadLocal.setSessionStore(sessionStore);
    }

    @Override
    public void onRequestReceived(ChannelHandlerContext ctx, HttpRequest e) {
        HttpSessionThreadLocal.unset();
        Collection<Cookie> cookies = Utils.getCookies(
                HttpSessionImpl.SESSION_ID_KEY, e);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String jsessionId = cookie.value();
                HttpSessionImpl s = HttpSessionThreadLocal.getSessionStore()
                        .findSession(jsessionId);
                if (s != null) {
                    HttpSessionThreadLocal.set(s);
                    this.sessionRequestedByCookie=true;
                    break;
                }
            }
        }

    }

    @Override
    public void onRequestSuccessed(ChannelHandlerContext ctx, HttpRequest e, HttpResponse response) {
        HttpSessionImpl s = HttpSessionThreadLocal.get();
        if (s != null && !this.sessionRequestedByCookie) {
//            HttpHeaders.addHeader(response, SET_COOKIE, ServerCookieEncoder.encode(HttpSessionImpl.SESSION_ID_KEY, s.getId()));
            response.headers().set(HttpHeaderNames.SET_COOKIE, io.netty.handler.codec.http.cookie.ServerCookieEncoder.STRICT.encode(HttpSessionImpl.SESSION_ID_KEY, s.getId()));
        }
    }

    @Override
    public void onRequestFailed(ChannelHandlerContext ctx, Throwable e, HttpResponse response) {
        this.sessionRequestedByCookie = false;
        HttpSessionThreadLocal.unset();
    }
}
