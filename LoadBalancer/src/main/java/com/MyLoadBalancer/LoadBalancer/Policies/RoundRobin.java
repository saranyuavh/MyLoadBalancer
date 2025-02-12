package com.MyLoadBalancer.LoadBalancer.Policies;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin implements ILoadBalancerPolicy{
    private static AtomicInteger backendServerInUse = new AtomicInteger();
    @Override
    public synchronized int getServerId() {
        if(backendServerInUse.get() == Integer.MAX_VALUE)
            backendServerInUse.set(-1);
        return backendServerInUse.incrementAndGet();
    }
}
