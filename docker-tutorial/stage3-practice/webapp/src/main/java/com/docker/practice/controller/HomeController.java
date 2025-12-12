package com.docker.practice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 主页控制器
 */
@Controller
public class HomeController {

    @Value("${spring.application.name:webapp}")
    private String appName;

    @GetMapping("/")
    public String index(Model model) {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            model.addAttribute("hostname", hostname);
            model.addAttribute("timestamp", timestamp);
            model.addAttribute("appName", appName);
            
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}

