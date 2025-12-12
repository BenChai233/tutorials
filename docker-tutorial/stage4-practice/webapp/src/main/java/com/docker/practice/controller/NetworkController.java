package com.docker.practice.controller;

import com.docker.practice.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 网络信息控制器
 */
@Controller
@RequestMapping("/")
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            model.addAttribute("hostname", hostname);
            model.addAttribute("ipAddress", ipAddress);
            model.addAttribute("timestamp", timestamp);
            model.addAttribute("appName", "Docker 网络实践");
            
            // 获取网络信息
            model.addAttribute("networkInfo", networkService.getNetworkInfo());
            model.addAttribute("serviceConnections", networkService.testServiceConnections());
            
            return "network";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/network/test")
    public String networkTest(Model model) {
        model.addAttribute("testResults", networkService.runNetworkTests());
        return "network-test";
    }
}

