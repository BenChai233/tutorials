package com.docker.practice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源监控服务
 * 用于演示容器资源限制和监控
 */
@Slf4j
@Service
public class ResourceService {

    /**
     * 获取JVM资源使用情况
     */
    public Map<String, Object> getJvmResources() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Runtime runtime = Runtime.getRuntime();

        Map<String, Object> resources = new HashMap<>();
        
        // 内存信息
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        resources.put("maxMemory", maxMemory);
        resources.put("totalMemory", totalMemory);
        resources.put("freeMemory", freeMemory);
        resources.put("usedMemory", usedMemory);
        resources.put("memoryUsagePercent", (usedMemory * 100.0) / maxMemory);
        
        // JVM信息
        resources.put("jvmName", runtimeBean.getVmName());
        resources.put("jvmVersion", runtimeBean.getVmVersion());
        resources.put("uptime", runtimeBean.getUptime());
        resources.put("availableProcessors", runtime.availableProcessors());
        
        // 堆内存信息
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        long heapCommitted = memoryBean.getHeapMemoryUsage().getCommitted();
        
        resources.put("heapUsed", heapUsed);
        resources.put("heapMax", heapMax);
        resources.put("heapCommitted", heapCommitted);
        resources.put("heapUsagePercent", (heapUsed * 100.0) / heapMax);
        
        return resources;
    }

    /**
     * CPU密集型操作（用于测试CPU限制）
     */
    public void performCpuIntensiveTask(int iterations) {
        log.info("开始CPU密集型任务，迭代次数: {}", iterations);
        long startTime = System.currentTimeMillis();
        
        double result = 0;
        for (int i = 0; i < iterations; i++) {
            result += Math.sqrt(i) * Math.sin(i) * Math.cos(i);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("CPU密集型任务完成，耗时: {}ms, 结果: {}", (endTime - startTime), result);
    }

    /**
     * 内存密集型操作（用于测试内存限制）
     */
    public void performMemoryIntensiveTask(int sizeMB) {
        log.info("开始内存密集型任务，分配大小: {}MB", sizeMB);
        try {
            byte[] data = new byte[sizeMB * 1024 * 1024];
            // 填充数据
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i % 256);
            }
            log.info("内存密集型任务完成，已分配 {}MB 内存", sizeMB);
            // 注意：这里不释放内存，用于测试内存限制
            Thread.sleep(5000); // 保持5秒
        } catch (OutOfMemoryError e) {
            log.error("内存不足，无法分配 {}MB 内存", sizeMB, e);
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("任务被中断", e);
        }
    }
}

