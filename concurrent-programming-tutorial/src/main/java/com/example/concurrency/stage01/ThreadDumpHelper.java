package com.example.concurrency.stage01;

import java.util.concurrent.CountDownLatch;

/**
 * 可选实验：用于配合 jstack/jcmd 观察线程状态。
 * 运行后会打印进程 PID，保持若干线程阻塞/等待，便于抓 dump。
 */
public class ThreadDumpHelper {

    public static void main(String[] args) throws Exception {
        int threads = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        CountDownLatch blocker = new CountDownLatch(1);
        System.out.println("PID hint (Unix-like): " + ProcessHandle.current().pid());
        System.out.println("Use: jcmd <pid> Thread.print  or  jstack <pid>");

        for (int i = 0; i < threads; i++) {
            int idx = i;
            new Thread(() -> {
                try {
                    if (idx % 2 == 0) {
                        synchronized (ThreadDumpHelper.class) {
                            blocker.await(); // WAITING
                        }
                    } else {
                        blocker.await(); // WAITING
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "dump-demo-" + idx).start();
        }

        // 主线程保持存活，等待手动 Ctrl+C 结束
        while (true) {
            Thread.sleep(5_000);
        }
    }
}
