package com.docker.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class JavaAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaAppApplication.class, args);
    }

    @RestController
    static class AppController {

        @GetMapping("/")
        public Map<String, Object> home() {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "🐳 Docker 镜像管理实践 - Java 应用");
            response.put("version", "1.0.0");
            response.put("environment", System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", "development"));
            response.put("timestamp", Instant.now().toString());
            return response;
        }

        @GetMapping("/health")
        public Map<String, String> health() {
            Map<String, String> response = new HashMap<>();
            response.put("status", "healthy");
            return response;
        }
    }
}

