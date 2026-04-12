package org.example.week3_threadpool;

import java.util.concurrent.*;

public class ThreadPoolConfigDemo {

    public static void main(String[] args) {
        // 自定义线程池
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                2,                          // corePoolSize
//                5,                          // maximumPoolSize
//                60,                         // keepAliveTime
//                TimeUnit.SECONDS,           // unit
//                new ArrayBlockingQueue<>(10), // 有界队列
//                Executors.defaultThreadFactory(),   //线程工厂
//                new ThreadPoolExecutor.AbortPolicy()  // 拒绝策略
//        );
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3, 5, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );


//        // 提交任务
//        for (int i = 0; i < 20; i++) {
//            final int taskId = i;
//            executor.submit(() -> {
//                System.out.println(Thread.currentThread().getName() + " 执行任务 " + taskId);
//            });
//        }
        // 提交 3 个任务
        for (int i = 0; i < 3; i++) {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName());
                try { Thread.sleep(10000); } catch (InterruptedException e) {}
            });
        }

        System.out.println("核心线程数: " + executor.getCorePoolSize());
        System.out.println("当前线程数: " + executor.getPoolSize());  // 应该是 3

        executor.shutdown();
    }
}