package org.example.week3_threadpool;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalFixDemo {

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    static class BigObject {
        private byte[] data = new byte[1024 * 1024];
        private int id;

        BigObject(int id) { this.id = id; }

        @Override
        protected void finalize() {
            System.out.println("BigObject " + id + " 被回收");
        }
    }

    // 错误示范：不 remove
    public static void testLeak() throws Exception {
        System.out.println("\n========== 错误示范：不 remove ==========");
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                ThreadLocal<BigObject> tl = new ThreadLocal<>();
                tl.set(new BigObject(taskId));
                System.out.println("任务 " + taskId + " 执行完，没有 remove");
            });
        }
        Thread.sleep(1000);
        System.gc();
        Thread.sleep(500);
    }

    // 正确示范：加 remove
    public static void testFix() throws Exception {
        System.out.println("\n========== 正确示范：加 remove ==========");
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                ThreadLocal<BigObject> tl = new ThreadLocal<>();
                try {
                    tl.set(new BigObject(taskId));
                    System.out.println("任务 " + taskId + " 执行");
                } finally {
                    tl.remove();  // 关键
                }
            });
        }
        Thread.sleep(1000);
        System.gc();
        Thread.sleep(500);
    }

    public static void main(String[] args) throws Exception {
        testLeak();
        System.out.println("\n按 Enter 继续测试正确版本...");
        System.in.read();
        testFix();

        executor.shutdown();
    }
}