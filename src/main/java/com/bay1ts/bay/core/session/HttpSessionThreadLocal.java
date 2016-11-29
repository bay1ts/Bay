package com.bay1ts.bay.core.session;

import com.bay1ts.bay.core.Config;

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

    public static HttpSessionImpl getOrCreate(String id) {
        if (HttpSessionThreadLocal.get() == null) {
            //因为在bay文件中 会对sessionthreadlocal初始化,给sessionstore赋值(根据配置),所以下面不需要了
//            if (sessionStore == null) {
//                sessionStore = new MemoryBasedSessionStore();
//            }
            HttpSessionImpl newSession=null;
            if (id!=null){
                newSession=sessionStore.findSession(id);
            }
            if (newSession==null){
                newSession= sessionStore.createSession();
            }
            newSession.setMaxInactiveInterval(Config.instance().getSessionExpireSecond());
            //已解决的bug 刚开始又 sessionStore.createSession了.
            sessionThreadLocal.set(newSession);
        }
        return get();
    }

}