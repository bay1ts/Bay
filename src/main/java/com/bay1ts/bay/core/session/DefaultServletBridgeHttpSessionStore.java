package com.bay1ts.bay.core.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServletBridgeHttpSessionStore implements
        BaseSessionStore {

//    private static final Logger log = LoggerFactory
//            .getLogger(DefaultServletBridgeHttpSessionStore.class);

    public static ConcurrentHashMap<String, ServletSessionImpl> sessions = new ConcurrentHashMap<String, ServletSessionImpl>();

    @Override
    public ServletSessionImpl createSession() {
        String sessionId = this.generateNewSessionId();
//        log.debug("Creating new session with id {}", sessionId);

        ServletSessionImpl session = new ServletSessionImpl(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    @Override
    public void destroySession(String sessionId) {
//        log.debug("Destroying session with id {}", sessionId);
        sessions.remove(sessionId);
    }

    @Override
    public ServletSessionImpl findSession(String sessionId) {
        if (sessionId == null)
            return null;

        return sessions.get(sessionId);
    }

    protected String generateNewSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void destroyInactiveSessions() {
        for (Map.Entry<String, ServletSessionImpl> entry : sessions.entrySet()) {
            ServletSessionImpl session = entry.getValue();
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