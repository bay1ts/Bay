package com.bay1ts.bay.core.session;

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
                sessionStore = new DefaultServletBridgeHttpSessionStore();
            }

            ServletSessionImpl newSession = sessionStore.createSession();
            // TODO: 2016/10/13 这里是写死的.应该是可以配置的
            newSession.setMaxInactiveInterval(3600);
            sessionThreadLocal.set(sessionStore.createSession());
        }
        return get();
    }

}