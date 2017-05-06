package com.alibaba.boot.dubbo.ratelimiting;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wuyu on 2017/5/6.
 */
public class RateLimiting {

    private String timeUnit;

    private Date time;

    private AtomicLong atomicLong = new AtomicLong(0);

    public Date getTime() {
        return time;
    }

    public RateLimiting setTime(Date time) {
        this.time = time;
        return this;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public RateLimiting setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public AtomicLong getAtomicLong() {
        return atomicLong;
    }

    public RateLimiting setAtomicLong(AtomicLong atomicLong) {
        this.atomicLong = atomicLong;
        return this;
    }
}
