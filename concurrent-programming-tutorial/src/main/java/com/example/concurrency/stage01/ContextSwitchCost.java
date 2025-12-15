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

    private static long singleThread(long iterations) {
        Instant start = Instant.now();
        long sum = 0;
        for (long i = 0; i < iterations; i++) {
            sum += i;
        }
        Instant end = Instant.now();
        if (sum == 42) {
            System.out.println("impossible branch"); // 防止被优化
        }
        return Duration.between(start, end).toNanos();
    }

    private static long multiThread(long iterations, int threads) throws InterruptedException {
        long slice = iterations / threads;
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threads);
        List<Thread> list = new ArrayList<>(threads);
        AtomicLong sink = new AtomicLong(); // 聚合结果，避免优化

        Instant start = Instant.now();
        for (int i = 0; i < threads; i++) {
            long begin = i * slice;
            long end = (i == threads - 1) ? iterations : begin + slice;
            Thread t = new Thread(() -> {
                try {
                    startGate.await();
                    long local = 0;
                    for (long x = begin; x < end; x++) {
                        local += x;
                    }
                    sink.addAndGet(local);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    endGate.countDown();
                }
            }, "ctx-" + i);
            list.add(t);
            t.start();
        }

        startGate.countDown(); // 同步起跑
        endGate.await();
        Instant finish = Instant.now();
        return Duration.between(start, finish).toNanos();
    }
}
