package com.alibaba.boot.dubbo.ratelimiting;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository storing data used by the gateway's rate limiting filter.
 */
public class RateLimitingRepository {

    private Map<String, RateLimiting> store = new ConcurrentHashMap<>();

    public void incrementCounter(String id, String timeUnit, Date time) {
        RateLimiting rateLimiting = store.get(id);
        if (rateLimiting == null) {
            rateLimiting = new RateLimiting()
                    .setTime(time)
                    .setTimeUnit(timeUnit);
            store.put(id, rateLimiting);
        }

        rateLimiting.getAtomicLong().incrementAndGet();
    }

    public long getCounter(String id, String timeUnit, Date time) {
        RateLimiting rateLimiting = store.get(id);

        if (rateLimiting == null) {
            rateLimiting = new RateLimiting()
                    .setTime(time)
                    .setTimeUnit(timeUnit);
            store.put(id, rateLimiting);
        }

        if ((time.getTime() - rateLimiting.getTime().getTime()) > toMilliSeconds(timeUnit)) {
            rateLimiting = new RateLimiting()
                    .setTime(time)
                    .setTimeUnit(timeUnit);
            store.put(id, rateLimiting);
        }

        return rateLimiting.getAtomicLong().get();
    }

    private long toMilliSeconds(String timeUnit) {
        if ("hours".equalsIgnoreCase(timeUnit)) {
            return 60 * 60 * 1000;
        } else if ("seconds".equalsIgnoreCase(timeUnit)) {
            return 1000;
        } else if ("days".equalsIgnoreCase(timeUnit)) {
            return 60 * 60 * 1000 * 24;
        } else if ("minutes".equalsIgnoreCase(timeUnit)) {
            return 60 * 1000;
        } else {
            return 0;
        }
    }

}
