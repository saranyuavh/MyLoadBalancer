package com.MyLoadBalancer.LoadBalancer.Policies.PolicyFactory;

import com.MyLoadBalancer.LoadBalancer.Policies.ILoadBalancerPolicy;
import com.MyLoadBalancer.LoadBalancer.Policies.RoundRobin;
import com.MyLoadBalancer.LoadBalancer.Policies.WeightedRoundRobin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadBalancerPolicyFactory {

//    private static LoadBalancerPolicyFactory factory;

//    public static LoadBalancerPolicyFactory getInstance() {
//        if(factory == null) factory = new LoadBalancerPolicyFactory();
//
//        return factory;
//    }
    private RoundRobin roundRobin;
    private WeightedRoundRobin weightedRoundRobin;

    @Autowired
    public void setRoundRobin(RoundRobin roundRobin) {
        this.roundRobin = roundRobin;
    }

    @Autowired
    public void setWeightedRoundRobin(WeightedRoundRobin weightedRoundRobin) {
        this.weightedRoundRobin = weightedRoundRobin;
    }
    public ILoadBalancerPolicy getPolicyImpl(String policyName) {
        ILoadBalancerPolicy policy = switch(policyName) {
            case "roundrobin" -> roundRobin;
            case "wroundrobin"  -> weightedRoundRobin;
            default -> throw new IllegalStateException("Incorrect configuration, check value: " + policyName);
        };

        return policy;
    }
}
