package com.docker.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 信息响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiInfo {
    private String hostname;
    private String timestamp;
    private String message;
    private Boolean volumeMounted;
    private String javaVersion;
    private String osName;
}

