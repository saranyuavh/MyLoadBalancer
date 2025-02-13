package com.MyLoadBalancer.LoadBalancer.Policies;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class WeightedRoundRobin implements ILoadBalancerPolicy{

    @Value("${backend.server.weights}")
    private String serverWeights;

    private int noOfWeights;
    private int[][] weightToServerId;

    private int backendServerId  = -1;

    private final ReentrantLock lock = new ReentrantLock(true);
    @PostConstruct
    public void init() {
        if(serverWeights == null || serverWeights.equals("")) throw new IllegalArgumentException("Weight for servers are not configured well, please check");
        List<String> weights = Arrays.asList(serverWeights.split(","));
        noOfWeights = weights.size();
        weightToServerId = new int[weights.size()][2];
        for(int i=0; i<noOfWeights; i++) {
            weightToServerId[i][0] = Integer.valueOf(weights.get(i));
            weightToServerId[i][1] = i;
        }
        Arrays.sort(weightToServerId, (a,b) -> a[0]-b[0]);
        for(int i=1; i<noOfWeights; i++) {
            weightToServerId[i][0] += weightToServerId[i-1][0];
        }
    }
    @Override
    public int getServerId() {
        int assignedServerId = 0;
        lock.lock();
        try {
            backendServerId += 1;
            if (backendServerId == weightToServerId[noOfWeights - 1][0]) {
                backendServerId = 0;
            }
            for (int i = 0; i < weightToServerId.length; i++) {
                if (weightToServerId[i][0] > backendServerId) {
                    assignedServerId = weightToServerId[i][1];
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        return assignedServerId;
    }
}
