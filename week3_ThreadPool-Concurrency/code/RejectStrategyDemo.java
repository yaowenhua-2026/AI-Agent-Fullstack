package org.example.week3_threadpool;

import java.util.concurrent.*;
public class RejectStrategyDemo {

    // 创建带拒绝策略的线程池
    public static void testRejectStrategy(String name, RejectedExecutionHandler handler) {
        System.out.println("\n========== " + name + " ==========");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,                          // 核心线程数（只有一个）
                1,                          // 最大线程数（只有一个）
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), // 队列容量只有 1
//                new ThreadPoolExecutor.AbortPolicy() // 先默认，后面换
//              new ThreadPoolExecutor.AbortPolicy()  // 拒绝策略
//              new ThreadPoolExecutor.CallerRunsPolicy()  // 让调用者执行
//              new ThreadPoolExecutor.DiscardPolicy()  // 静默丢弃
              new ThreadPoolExecutor.DiscardOldestPolicy()  // 丢弃最老的任务

        );

        // 提交 3 个任务（核心1个 + 队列1个 = 容量2，第3个会触发拒绝）
        for (int i = 0; i < 4; i++) {
            final int taskId = i;
            try {
                executor.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + " 执行任务 " + taskId);
                    try { Thread.sleep(500); } catch (InterruptedException e) {}
                });
            } catch (RejectedExecutionException e) {
                System.out.println("任务 " + taskId + " 被拒绝: " + e.getMessage());
            }
        }

        executor.shutdown();
    }

    public static void main(String[] args) {
        testRejectStrategy("DiscardOldestPolicy", new ThreadPoolExecutor.AbortPolicy());
    }
}