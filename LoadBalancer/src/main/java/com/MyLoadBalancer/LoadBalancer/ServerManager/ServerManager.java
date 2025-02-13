package com.MyLoadBalancer.LoadBalancer.ServerManager;

import com.MyLoadBalancer.LoadBalancer.LoadBalancerApplication;
import com.MyLoadBalancer.LoadBalancer.Policies.ILoadBalancerPolicy;
import com.MyLoadBalancer.LoadBalancer.Policies.PolicyFactory.LoadBalancerPolicyFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.NotActiveException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/serversHealth")
@EnableScheduling
@Component
public class ServerManager {


    @Value("${backend.server.health.urls}")
    String serverHealthUrls;

    @Value("${lb.policy.name}")
    String policyName;

    @Value("${backend.server.urls}")
    String serverUrls;

    private Map<String, Boolean> serverHealthMap;

    private List<String> serverHealthUrlsList;

    private List<String> serverUrlsList;

    private RestTemplate restTemplate;

    private ILoadBalancerPolicy loadBalancerPolicy;


    private LoadBalancerPolicyFactory factory;

    @PostConstruct
    public void init() {
        serverHealthUrlsList = Arrays.asList((serverHealthUrls).split(","));
        serverUrlsList = Arrays.asList(serverUrls.split(","));

        restTemplate = new RestTemplate();
        loadBalancerPolicy = this.factory.getPolicyImpl(policyName.toLowerCase());
        serverHealthMap = new HashMap<>();

        for(String serverUrl : serverHealthUrlsList) {
            serverHealthMap.put(serverUrl, false);
        }
    }

    @Autowired
    public void setLoadBalancerPolicyFactory(LoadBalancerPolicyFactory factory) {
        this.factory = factory;
    }
    public String getHealthyServer() throws NotActiveException {
        int attempts = 0;
        int totalAttempts = 0;
        int noOfServers = serverHealthUrlsList.size();

        while(totalAttempts < 3) {
            attempts = 0;
            while (attempts < noOfServers) {
                int serverId = loadBalancerPolicy.getServerId();
                if (serverHealthMap.getOrDefault(serverHealthUrlsList.get(serverId % noOfServers), false)) {
                    System.out.println(serverId % noOfServers +" actual server number");
                    return serverUrlsList.get(serverId % noOfServers);
                }
                attempts++;
            }
            try {
                System.out.println("No healthy servers found, waiting for 10s to recover");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() +" is interrupted");
            }
            totalAttempts++;
        }

        throw new NotActiveException("Healthy servers unavailable");
    }

    @GetMapping("/check")
    @Scheduled(fixedRateString = "${lb.healthCheck.freq}")
    public ResponseEntity<String> healthCheckExecutor() {
//        System.out.println("Starting Health check \n");
        for(String serverUrl : serverHealthUrlsList) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(serverUrl, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
                if (response.getStatusCode().value() == 200) {
                    serverHealthMap.put(serverUrl, true);
                } else {
                    serverHealthMap.put(serverUrl, false);
                }
            } catch (ResourceAccessException e) {
                serverHealthMap.put(serverUrl, false);
                System.out.println(e.getMessage());
            }
        }
//        System.out.println(serverHealthMap);
//        System.out.println("Health check completed \n");
        return ResponseEntity.ok("Health Check done \n");
    }
}
