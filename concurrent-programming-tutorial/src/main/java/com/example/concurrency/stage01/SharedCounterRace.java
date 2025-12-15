package com.example.concurrency.stage01;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实验 3：共享变量竞态演示。
 * 版本 A：无同步，丢失更新；版本 B：使用 Atomic 或 synchronized。
 */
public class SharedCounterRace {

    public static void main(String[] args) throws InterruptedException {
        int threads = args.length > 0 ? Integer.parseInt(args[0]) : 8;
        int increments = args.length > 1 ? Integer.parseInt(args[1]) : 500_000;
        System.out.printf("SharedCounterRace threads=%d increments=%d%n", threads, increments);

        runUnsafely(threads, increments);
        runWithAtomic(threads, increments);
        runWithSynchronized(threads, increments);
    }

    private static void runUnsafely(int threads, int increments) throws InterruptedException {
        int[] counter = new int[1]; // 故意共享非线程安全变量
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < increments; j++) {
                        counter[0]++; // 非原子操作：读-改-写
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }, "race-" + i).start();
        }
        long begin = System.nanoTime();
        start.countDown();
        done.await();
        long cost = System.nanoTime() - begin;
        System.out.printf("unsafe   result=%d expected=%d cost=%.3f ms%n",
                counter[0], threads * increments, cost / 1_000_000.0);
    }

    private static void runWithAtomic(int threads, int increments) throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < increments; j++) {
                        counter.incrementAndGet();
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }, "atomic-" + i).start();
        }
        long begin = System.nanoTime();
        start.countDown();
        done.await();
        long cost = System.nanoTime() - begin;
        System.out.printf("atomic   result=%d expected=%d cost=%.3f ms%n",
                counter.get(), threads * increments, cost / 1_000_000.0);
    }

    private static void runWithSynchronized(int threads, int increments) throws InterruptedException {
        class Counter {
            private int value;
            synchronized void inc() { value++; }
            synchronized int get() { return value; }
        }
        Counter counter = new Counter();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < increments; j++) {
                        counter.inc();
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }, "sync-" + i).start();
        }
        long begin = System.nanoTime();
        start.countDown();
        done.await();
        long cost = System.nanoTime() - begin;
        System.out.printf("sync     result=%d expected=%d cost=%.3f ms%n",
                counter.get(), threads * increments, cost / 1_000_000.0);
    }
}
