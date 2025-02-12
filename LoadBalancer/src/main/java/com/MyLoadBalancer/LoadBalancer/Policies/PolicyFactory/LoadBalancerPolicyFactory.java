package com.MyLoadBalancer.LoadBalancer.Policies.PolicyFactory;

import com.MyLoadBalancer.LoadBalancer.Policies.ILoadBalancerPolicy;
import com.MyLoadBalancer.LoadBalancer.Policies.RoundRobin;

public class LoadBalancerPolicyFactory {

    private static LoadBalancerPolicyFactory factory;

    public static LoadBalancerPolicyFactory getInstance() {
        if(factory == null) factory = new LoadBalancerPolicyFactory();

        return factory;
    }

    public ILoadBalancerPolicy getPolicyImpl(String policyName) {
        ILoadBalancerPolicy policy = switch(policyName) {
            case "roundrobin" -> new RoundRobin();
            default -> throw new IllegalStateException("Incorrect configuration, check value: " + policyName);
        };

        return policy;
    }
}
