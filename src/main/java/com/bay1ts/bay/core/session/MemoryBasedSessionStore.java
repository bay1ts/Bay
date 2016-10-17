package com.bay1ts.bay.core.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryBasedSessionStore implements
        BaseSessionStore {

//    private static final Logger log = LoggerFactory
//            .getLogger(MemoryBasedSessionStore.class);

    public static ConcurrentHashMap<String, HttpSessionImpl> sessions = new ConcurrentHashMap<String, HttpSessionImpl>();

    @Override
    public HttpSessionImpl createSession() {
        String sessionId = this.generateNewSessionId();
//        log.debug("Creating new session with id {}", sessionId);

        HttpSessionImpl session = new HttpSessionImpl(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    @Override
    public void destroySession(String sessionId) {
//        log.debug("Destroying session with id {}", sessionId);
        sessions.remove(sessionId);
    }

    @Override
    public HttpSessionImpl findSession(String sessionId) {
        if (sessionId == null)
            return null;

        return sessions.get(sessionId);
    }

    protected String generateNewSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void destroyInactiveSessions() {
        for (Map.Entry<String, HttpSessionImpl> entry : sessions.entrySet()) {
            HttpSessionImpl session = entry.getValue();
            if (session.getMaxInactiveInterval() < 0)
                continue;

            long currentMillis = System.currentTimeMillis();

            if (currentMillis - session.getLastAccessedTime() > session
                    .getMaxInactiveInterval() * 1000) {

                destroySession(entry.getKey());
            }
        }
    }

}