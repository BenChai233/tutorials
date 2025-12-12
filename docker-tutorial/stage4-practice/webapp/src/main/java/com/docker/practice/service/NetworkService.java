package com.docker.practice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.*;

/**
 * 网络服务 - 用于测试容器间网络通信
 */
@Service
public class NetworkService {

    @Value("${app.api-service.url}")
    private String apiServiceUrl;

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取网络信息
     */
    public Map<String, Object> getNetworkInfo() {
        Map<String, Object> info = new HashMap<>();
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            info.put("hostname", localhost.getHostName());
            info.put("ipAddress", localhost.getHostAddress());
            info.put("canonicalHostName", localhost.getCanonicalHostName());
        } catch (Exception e) {
            info.put("error", e.getMessage());
        }
        return info;
    }

    /**
     * 测试服务连接
     */
    public Map<String, Object> testServiceConnections() {
        Map<String, Object> connections = new HashMap<>();
        
        // 测试 API 服务
        connections.put("apiService", testApiService());
        
        // 测试 Redis
        connections.put("redis", testRedis());
        
        // 测试 MySQL (通过 API 服务)
        connections.put("mysql", testMysqlViaApi());
        
        return connections;
    }

    /**
     * 测试 API 服务连接
     */
    private Map<String, Object> testApiService() {
        Map<String, Object> result = new HashMap<>();
        try {
            WebClient webClient = WebClient.create(apiServiceUrl);
            Mono<Map> response = webClient.get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(Map.class);
            
            Map<String, Object> health = response.block();
            result.put("status", "connected");
            result.put("response", health);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 测试 Redis 连接
     */
    private Map<String, Object> testRedis() {
        Map<String, Object> result = new HashMap<>();
        try {
            if (redisTemplate != null) {
                String testKey = "network_test_" + System.currentTimeMillis();
                redisTemplate.opsForValue().set(testKey, "test_value");
                String value = redisTemplate.opsForValue().get(testKey);
                redisTemplate.delete(testKey);
                
                result.put("status", "connected");
                result.put("testValue", value);
            } else {
                result.put("status", "not_configured");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 通过 API 服务测试 MySQL
     */
    private Map<String, Object> testMysqlViaApi() {
        Map<String, Object> result = new HashMap<>();
        try {
            WebClient webClient = WebClient.create(apiServiceUrl);
            Mono<Map> response = webClient.get()
                    .uri("/api/mysql/test")
                    .retrieve()
                    .bodyToMono(Map.class);
            
            Map<String, Object> mysqlTest = response.block();
            result.put("status", "connected");
            result.put("response", mysqlTest);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 运行网络测试
     */
    public Map<String, Object> runNetworkTests() {
        Map<String, Object> results = new HashMap<>();
        results.put("timestamp", new Date());
        results.put("tests", testServiceConnections());
        return results;
    }
}

