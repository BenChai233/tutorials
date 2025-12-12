package com.docker.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * API 数据响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiData {
    private List<User> users;
    private Stats stats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String name;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        private Integer totalUsers;
        private Integer activeUsers;
    }
}

