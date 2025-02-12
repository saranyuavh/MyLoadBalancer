package com.myloadbalancer.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class BackendController {

    @GetMapping("")
    public String handleRequest(HttpServletRequest request, @RequestHeader Map<String, String> headers) {
        StringBuilder loggerStr = new StringBuilder();
        loggerStr.append("./be").append("\n")
                .append("Received request from ").append(request.getRemoteAddr()).append("\n")
                .append(request.getMethod()).append(" / ").append(request.getProtocol()).append("\n")
                .append("Host ").append(headers.get("host")).append("\n")
                .append("User-Agent ").append(headers.get("user-agent")).append("\n")
                .append("Accept ").append(headers.get("accept"));
        System.out.println(loggerStr.toString());
        System.out.println("\nReplied with a hello message");
        return "Hello From Backend Server";
    }
}
