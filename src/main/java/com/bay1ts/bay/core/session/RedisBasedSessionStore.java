package com.bay1ts.bay.core.session;

import com.bay1ts.bay.core.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.UUID;

/**
 * Created by chenu on 2016/10/15.
 */
public class RedisBasedSessionStore implements BaseSessionStore {
    private Logger logger= LoggerFactory.getLogger(RedisBasedSessionStore.class);
    private JedisConnectionFactory factory;

    private RedisTemplate<String, HttpSessionImpl> redisTemplate;
    public RedisBasedSessionStore(){
        logger.info("Init Redis based session store");
        this.factory=new JedisConnectionFactory();
        this.redisTemplate=new RedisTemplate<>();
        factory.setDatabase(0);
        factory.setHostName(Config.instance().getRedisLocate());
        factory.setPort(Config.instance().getPort());
        factory.setPassword(Config.instance().getRedisPassword());
        redisTemplate.setConnectionFactory(factory);
    }

    @Override
    public HttpSessionImpl findSession(String sessionId) {
        if (sessionId == null)
            return null;
        return redisTemplate.opsForValue().get(sessionId);
    }

    @Override
    public HttpSessionImpl createSession() {
        String sessionId = generateNewSessionId();
        HttpSessionImpl session = new HttpSessionImpl(sessionId);
        redisTemplate.opsForValue().set(sessionId, session);
        return session;
    }

    @Override
    public void destroySession(String sessionId) {
        redisTemplate.delete(sessionId);
    }

    @Override
    public void destroyInactiveSessions() {
        Set<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().keys("*".getBytes());
        HttpSessionImpl session = null;
        for (byte[] key : keys) {
            session = redisTemplate.opsForValue().get(key);
            if (session.getMaxInactiveInterval() < 0)
                continue;
            long currentMillis = System.currentTimeMillis();

            if (currentMillis - session.getLastAccessedTime() > session
                    .getMaxInactiveInterval() * 1000) {
                destroySession(new String(key));
            }

        }
    }

    protected String generateNewSessionId() {
        return UUID.randomUUID().toString();
    }
}
