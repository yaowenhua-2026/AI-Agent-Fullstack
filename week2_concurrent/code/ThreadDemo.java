package org.example.week2_Threadlife;

public class ThreadDemo {
    public static void main(String[] args) throws Exception {

        // 1. NEW 状态
        Thread t = new Thread(() -> {
            System.out.println("线程执行中...");
            try {
                Thread.sleep(2000);  // 让当前线程（t）休眠
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("1. 刚创建: " + t.getState());  // NEW

        // 2. RUNNABLE 状态
        t.start();
        System.out.println("2. start后: " + t.getState());  // RUNNABLE

        // 3. TIMED_WAITING 状态（t 进入 sleep）
        Thread.sleep(500);  // 让 main 等一会，等 t 进入 sleep
        System.out.println("3. t sleep时: " + t.getState());  // TIMED_WAITING

        // 4. TERMINATED 状态（t 执行完）
        Thread.sleep(3000);  // 等 t 执行完
        System.out.println("4. 执行完后: " + t.getState());  // TERMINATED
    }



}