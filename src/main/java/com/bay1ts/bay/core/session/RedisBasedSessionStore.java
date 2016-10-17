package com.bay1ts.bay.core.session;

/**
 * Created by chenu on 2016/10/15.
 */
public class RedisBasedSessionStore implements  BaseSessionStore {
    @Override
    public HttpSessionImpl findSession(String sessionId) {
        return null;
    }

    @Override
    public HttpSessionImpl createSession() {
        return null;
    }

    @Override
    public void destroySession(String sessionId) {

    }

    @Override
    public void destroyInactiveSessions() {

    }
}
