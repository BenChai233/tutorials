package com.docker.practice.controller;

import com.docker.practice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 资源监控控制器
 * 用于演示容器资源限制和监控
 */
@Slf4j
@Controller
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * 资源监控页面
     */
    @GetMapping
    public String resourcesPage(Model model) {
        model.addAttribute("resources", resourceService.getJvmResources());
        return "resources";
    }

    /**
     * 获取资源信息的API
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getResources() {
        return ResponseEntity.ok(resourceService.getJvmResources());
    }

    /**
     * CPU密集型测试
     */
    @PostMapping("/cpu-test")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cpuTest(
            @RequestParam(defaultValue = "10000000") int iterations) {
        log.info("收到CPU测试请求，迭代次数: {}", iterations);
        long startTime = System.currentTimeMillis();
        
        try {
            resourceService.performCpuIntensiveTask(iterations);
            long duration = System.currentTimeMillis() - startTime;
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "iterations", String.valueOf(iterations),
                "duration", duration + "ms"
            ));
        } catch (Exception e) {
            log.error("CPU测试失败", e);
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 内存密集型测试
     */
    @PostMapping("/memory-test")
    @ResponseBody
    public ResponseEntity<Map<String, String>> memoryTest(
            @RequestParam(defaultValue = "100") int sizeMB) {
        log.info("收到内存测试请求，大小: {}MB", sizeMB);
        
        try {
            resourceService.performMemoryIntensiveTask(sizeMB);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "allocated", sizeMB + "MB"
            ));
        } catch (OutOfMemoryError e) {
            log.error("内存测试失败：内存不足", e);
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "message", "OutOfMemoryError: " + e.getMessage()
            ));
        } catch (Exception e) {
            log.error("内存测试失败", e);
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}

