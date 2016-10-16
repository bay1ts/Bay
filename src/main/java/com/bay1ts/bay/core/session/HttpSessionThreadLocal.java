package com.bay1ts.bay.core.session;

import com.bay1ts.bay.Config;

public class HttpSessionThreadLocal {

    public static final ThreadLocal<ServletSessionImpl> sessionThreadLocal = new ThreadLocal<ServletSessionImpl>();

    private static BaseSessionStore sessionStore;

    public static BaseSessionStore getSessionStore() {
        return sessionStore;
    }

    public static void setSessionStore(BaseSessionStore store) {
        sessionStore = store;
    }

    public static void set(ServletSessionImpl session) {
        sessionThreadLocal.set(session);
    }

    public static void unset() {
        sessionThreadLocal.remove();
    }

    public static ServletSessionImpl get() {
        ServletSessionImpl session = sessionThreadLocal.get();
        if (session != null)
            session.touch();
        return session;
    }

    public static ServletSessionImpl getOrCreate() {
        if (HttpSessionThreadLocal.get() == null) {
            if (sessionStore == null) {
                // TODO: 2016/10/16 可配置的sessionstore
                sessionStore = new MemoryBasedSessionStore();
            }

            ServletSessionImpl newSession = sessionStore.createSession();
            newSession.setMaxInactiveInterval(Config.getSessionExpireSecond());
            //已解决的bug 刚开始又 sessionStore.createSession了.
            sessionThreadLocal.set(newSession);
        }
        return get();
    }

}