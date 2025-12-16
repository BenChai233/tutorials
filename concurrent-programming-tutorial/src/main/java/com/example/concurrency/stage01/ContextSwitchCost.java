package com.example.concurrency.stage01;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 实验 2：上下文切换成本感知。
 * 基准对比：单线程循环 vs 多线程分片。
 */
public class ContextSwitchCost {

    public static void main(String[] args) throws InterruptedException {
        long iterations = args.length > 0 ? Long.parseLong(args[0]) : 100_000_000L;
        int threads = args.length > 1 ? Integer.parseInt(args[1]) : Runtime.getRuntime().availableProcessors();

        System.out.printf("ContextSwitchCost iterations=%d threads=%d%n", iterations, threads);
        long singleNs = singleThread(iterations);
        long multiNs = multiThread(iterations, threads);

        System.out.printf("single-thread:  %,d ns (%.3f ms)%n", singleNs, singleNs / 1_000_000.0);
        System.out.printf("multi-thread :  %,d ns (%.3f ms)%n", multiNs, multiNs / 1_000_000.0);
    }


    /**
     * 单线程方式执行累加操作
     * 
     * @param iterations 总迭代次数
     * @return 执行耗时（纳秒）
     */
    private static long singleThread(long iterations) {
        // 记录开始时间
        Instant start = Instant.now();
        
        // 使用局部变量累加
        long sum = 0;
        for (long i = 0; i < iterations; i++) {
            sum += i;
        }
        
        // 记录结束时间
        Instant end = Instant.now();
        
        // 防止 JIT 编译器优化掉整个计算逻辑
        // 使用一个永远不会满足的条件，确保 sum 被实际使用
        if (sum == 42) {
            System.out.println("impossible branch"); // 防止被优化
        }
        
        // 返回总耗时（纳秒）
        return Duration.between(start, end).toNanos();
    }


    /**
     * 多线程方式执行累加操作
     * 
     * @param iterations 总迭代次数
     * @param threads 线程数量
     * @return 执行耗时（纳秒）
     * @throws InterruptedException 如果等待过程被中断
     */
    private static long multiThread(long iterations, int threads) throws InterruptedException {
        // 计算每个线程处理的数据分片大小
        long slice = iterations / threads;
        
        // startGate：控制所有线程同时开始执行，确保公平竞争
        CountDownLatch startGate = new CountDownLatch(1);
        // endGate：等待所有线程执行完毕
        CountDownLatch endGate = new CountDownLatch(threads);

        // 保存线程引用列表
        List<Thread> list = new ArrayList<>(threads);
        // 使用 AtomicLong 聚合各线程的计算结果，防止编译器优化掉计算逻辑
        AtomicLong sink = new AtomicLong();

        // 记录开始时间（包含线程创建和启动的开销）
        Instant start = Instant.now();
        
        // 创建并启动所有工作线程
        for (int i = 0; i < threads; i++) {
            // 计算当前线程负责的数据范围
            long begin = i * slice;
            // 最后一个线程处理剩余的所有数据，避免因整除导致的数据遗漏
            long end = (i == threads - 1) ? iterations : begin + slice;
            
            Thread t = new Thread(() -> {
                try {
                    // 等待 startGate 开启，确保所有线程同时开始计算
                    startGate.await();
                    
                    // 使用局部变量累加，减少原子操作次数，提升性能
                    long local = 0;
                    for (long x = begin; x < end; x++) {
                        local += x;
                    }
                    
                    // 将局部结果原子地累加到全局结果中
                    sink.addAndGet(local);
                } catch (InterruptedException ignored) {
                    // 恢复中断状态
                    Thread.currentThread().interrupt();
                } finally {
                    // 无论是否异常，都要通知 endGate 当前线程已完成
                    endGate.countDown();
                }
            }, "ctx-" + i); // 为线程命名，便于调试

            list.add(t);
            t.start(); // 启动线程
        }

        // 打开起跑门，所有线程同时开始执行
        startGate.countDown();
        // 等待所有线程完成工作
        endGate.await();

        // 记录结束时间
        Instant finish = Instant.now();
        // 返回总耗时（纳秒）
        return Duration.between(start, finish).toNanos();
    }
}
