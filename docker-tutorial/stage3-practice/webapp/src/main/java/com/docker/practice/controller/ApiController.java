package com.docker.practice.controller;

import com.docker.practice.dto.ApiInfo;
import com.docker.practice.dto.ApiData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * REST API 控制器
 */
@RestController
public class ApiController {

    @GetMapping("/api/info")
    public ApiInfo getInfo() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String javaVersion = System.getProperty("java.version");
            String osName = System.getProperty("os.name");
            
            return ApiInfo.builder()
                    .hostname(hostname)
                    .timestamp(LocalDateTime.now().toString())
                    .message("这是使用绑定挂载运行的 Spring Boot 应用")
                    .volumeMounted(true)
                    .javaVersion(javaVersion)
                    .osName(osName)
                    .build();
        } catch (Exception e) {
            return ApiInfo.builder()
                    .message("获取信息失败: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/api/data")
    public ApiData getData() {
        List<ApiData.User> users = Arrays.asList(
                new ApiData.User(1, "Alice", "Admin"),
                new ApiData.User(2, "Bob", "User"),
                new ApiData.User(3, "Charlie", "User")
        );
        
        return ApiData.builder()
                .users(users)
                .stats(new ApiData.Stats(3, 2))
                .build();
    }
}

