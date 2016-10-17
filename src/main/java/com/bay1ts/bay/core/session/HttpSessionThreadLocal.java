package com.bay1ts.bay.core.session;

import com.bay1ts.bay.Config;

public class HttpSessionThreadLocal {

    public static final ThreadLocal<HttpSessionImpl> sessionThreadLocal = new ThreadLocal<HttpSessionImpl>();

    private static BaseSessionStore sessionStore;

    public static BaseSessionStore getSessionStore() {
        return sessionStore;
    }

    // TODO: 2016/10/16 参看 httpsessioninterceptor.java line 35
    public static void setSessionStore(BaseSessionStore store) {
        sessionStore = store;
    }

    public static void set(HttpSessionImpl session) {
        sessionThreadLocal.set(session);
    }

    public static void unset() {
        sessionThreadLocal.remove();
    }

    public static HttpSessionImpl get() {
        HttpSessionImpl session = sessionThreadLocal.get();
        if (session != null)
            session.touch();
        return session;
    }

    public static HttpSessionImpl getOrCreate() {
        if (HttpSessionThreadLocal.get() == null) {
            if (sessionStore == null) {
                // TODO: 2016/10/16 可配置的sessionstore
                sessionStore = new MemoryBasedSessionStore();
            }

            HttpSessionImpl newSession = sessionStore.createSession();
            newSession.setMaxInactiveInterval(Config.getSessionExpireSecond());
            //已解决的bug 刚开始又 sessionStore.createSession了.
            sessionThreadLocal.set(newSession);
        }
        return get();
    }

}