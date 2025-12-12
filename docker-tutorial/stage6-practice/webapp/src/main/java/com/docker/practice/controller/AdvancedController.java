package com.docker.practice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 高级特性演示控制器
 * 用于演示Docker高级主题的各种功能
 */
@Slf4j
@Controller
@RequestMapping("/advanced")
public class AdvancedController {

    /**
     * 高级特性展示页面
     */
    @GetMapping
    public String advancedPage(Model model) {
        model.addAttribute("currentTime", LocalDateTime.now());
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("userName", System.getProperty("user.name"));
        return "advanced";
    }

    /**
     * 生成日志的API（用于测试日志管理）
     */
    @GetMapping("/generate-logs")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateLogs() {
        log.trace("这是一条 TRACE 级别的日志");
        log.debug("这是一条 DEBUG 级别的日志");
        log.info("这是一条 INFO 级别的日志 - 正常操作");
        log.warn("这是一条 WARN 级别的日志 - 警告信息");
        log.error("这是一条 ERROR 级别的日志 - 错误信息（模拟）");
        
        // 生成多条日志
        for (int i = 0; i < 10; i++) {
            log.info("批量日志生成 - 第 {} 条", i + 1);
        }
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "已生成多条不同级别的日志，请查看容器日志"
        ));
    }

    /**
     * 健康检查信息
     */
    @GetMapping("/health-info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> healthInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("timestamp", LocalDateTime.now());
        info.put("uptime", System.currentTimeMillis());
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemory", Runtime.getRuntime().maxMemory());
        info.put("totalMemory", Runtime.getRuntime().totalMemory());
        info.put("freeMemory", Runtime.getRuntime().freeMemory());
        
        return ResponseEntity.ok(info);
    }

    /**
     * 系统信息
     */
    @GetMapping("/system-info")
    @ResponseBody
    public ResponseEntity<Map<String, String>> systemInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("osArch", System.getProperty("os.arch"));
        info.put("userName", System.getProperty("user.name"));
        info.put("userHome", System.getProperty("user.home"));
        info.put("javaHome", System.getProperty("java.home"));
        
        return ResponseEntity.ok(info);
    }
}

