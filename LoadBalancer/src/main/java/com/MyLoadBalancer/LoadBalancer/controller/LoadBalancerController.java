package com.MyLoadBalancer.LoadBalancer.controller;


import com.MyLoadBalancer.LoadBalancer.ServerManager.ServerManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.NotActiveException;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class LoadBalancerController {

    private final RestTemplate restTemplate;

    static HashMap<String, Integer> map = new HashMap<>();

    private ServerManager serverManager;
    public LoadBalancerController() {
        this.restTemplate = new RestTemplate();
    }


    @Autowired
    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @GetMapping("")
    public ResponseEntity<String> handleRequest(HttpServletRequest request, @RequestHeader Map<String, String> headers) {
        StringBuilder loggerStr = new StringBuilder();
        loggerStr.append("./lb").append("\n ")
                .append("Received request from ").append(request.getRemoteAddr()).append("\n")
                .append(request.getMethod()).append(" / ").append(request.getProtocol()).append("\n")
                .append("Host ").append(headers.get("host")).append("\n")
                .append("User-Agent ").append(headers.get("user-agent")).append("\n")
                .append("Accept ").append(headers.get("accept")).append("\n");
        System.out.println(loggerStr);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAll(headers);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);

        String backendUrl;

        try {
             backendUrl = serverManager.getHealthyServer();
             map.put(backendUrl, map.getOrDefault(backendUrl, 0)+1);
        } catch (NotActiveException e) {
            System.out.println(e.getMessage() + ": No servers available to process requests, returning 500");
            return ResponseEntity.internalServerError().body("Servers Unavailable");
        }

        System.out.println("Request to backend server hosted at "+ backendUrl);

        ResponseEntity<String> response = restTemplate.exchange(backendUrl, HttpMethod.GET, requestEntity, String.class);


        StringBuilder responseLogger = new StringBuilder();
        responseLogger.append("Response from server:")
                        .append(request.getProtocol()).append(" ")
                                .append(response.getStatusCode()).append("\n");
        System.out.println(responseLogger);
        System.out.println(map);
        return response;
    }

}
