package com.docker.practice.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Docker Compose 服务类
 * 用于获取和展示 Compose 相关信息
 */
@Service
@RequiredArgsConstructor
public class ComposeService {

    private final Environment environment;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.application.name:webapp}")
    private String applicationName;

    /**
     * 获取 Compose 基本信息
     */
    public ComposeInfo getComposeInfo() {
        ComposeInfo info = new ComposeInfo();
        info.setApplicationName(applicationName);
        info.setCurrentTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        info.setProfile(environment.getProperty("spring.profiles.active", "default"));
        info.setServerPort(environment.getProperty("server.port", "8080"));
        return info;
    }

    /**
     * 获取服务状态
     */
    public Map<String, ServiceStatus> getServicesStatus() {
        Map<String, ServiceStatus> services = new HashMap<>();

        // MySQL 状态
        ServiceStatus mysql = new ServiceStatus();
        mysql.setName("MySQL");
        mysql.setHost(environment.getProperty("spring.datasource.url", "未配置"));
        mysql.setStatus(testMysqlConnection() ? "运行中" : "未连接");
        services.put("mysql", mysql);

        // Redis 状态
        ServiceStatus redis = new ServiceStatus();
        redis.setName("Redis");
        redis.setHost(environment.getProperty("spring.redis.host", "未配置") + ":" +
                environment.getProperty("spring.redis.port", "6379"));
        redis.setStatus(testRedisConnection() ? "运行中" : "未连接");
        services.put("redis", redis);

        return services;
    }

    /**
     * 获取环境信息
     */
    public Map<String, String> getEnvironmentInfo() {
        Map<String, String> env = new HashMap<>();
        env.put("Java版本", System.getProperty("java.version"));
        env.put("操作系统", System.getProperty("os.name"));
        env.put("时区", environment.getProperty("TZ", "未设置"));
        env.put("活跃配置", environment.getProperty("spring.profiles.active", "default"));
        return env;
    }

    /**
     * 测试 MySQL 连接
     */
    private boolean testMysqlConnection() {
        try {
            String url = environment.getProperty("spring.datasource.url");
            return url != null && url.contains("mysql");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 测试 Redis 连接
     */
    private boolean testRedisConnection() {
        try {
            redisTemplate.opsForValue().get("test");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    public static class ComposeInfo {
        private String applicationName;
        private String currentTime;
        private String profile;
        private String serverPort;
    }

    @Data
    public static class ServiceStatus {
        private String name;
        private String host;
        private String status;
    }
}

