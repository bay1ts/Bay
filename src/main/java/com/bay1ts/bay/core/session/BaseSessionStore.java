package com.bay1ts.bay.core.session;

public interface BaseSessionStore {

    HttpSessionImpl findSession(String sessionId);

    HttpSessionImpl createSession();

    void destroySession(String sessionId);

    void destroyInactiveSessions();

}