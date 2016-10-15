package com.bay1ts.bay.core.session;

/**
 * Created by chenu on 2016/10/15.
 */
public class RedisBasedSessionStore implements  BaseSessionStore {
    @Override
    public ServletSessionImpl findSession(String sessionId) {
        return null;
    }

    @Override
    public ServletSessionImpl createSession() {
        return null;
    }

    @Override
    public void destroySession(String sessionId) {

    }

    @Override
    public void destroyInactiveSessions() {

    }
}
