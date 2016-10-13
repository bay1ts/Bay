package com.bay1ts.bay.core.session;

import com.bay1ts.bay.core.session.ServletSessionImpl;

public interface BaseSessionStore {

    ServletSessionImpl findSession(String sessionId);

    ServletSessionImpl createSession();

    void destroySession(String sessionId);

    void destroyInactiveSessions();

}