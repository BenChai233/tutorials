package com.example.concurrency.stage01;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 实验 1：线程基础与调度观察。
 * 创建若干线程打印标识，观察输出交错和 join 行为。
 */
public class ThreadBasics {

    public static void main(String[] args) throws InterruptedException {
        int threads = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        int loops = args.length > 1 ? Integer.parseInt(args[1]) : 5;
        System.out.printf("starting ThreadBasics with threads=%d, loops=%d%n", threads, loops);

        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            int idx = i;
            Thread t = new Thread(() -> run(idx, loops), "demo-" + idx);
            list.add(t);
            t.start();
        }

        for (Thread t : list) {
            t.join();
        }
        System.out.println(ts() + " [main] done");
    }

    private static void run(int idx, int loops) {
        for (int i = 0; i < loops; i++) {
            System.out.printf("%s [%s] tick %d%n", ts(), Thread.currentThread().getName(), i);
            try {
                Thread.sleep(10); // 给调度器机会交错输出
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static String ts() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }
}
