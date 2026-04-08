package org.example.week2_Threadlife;

public class VisibilityDemo {

    private static boolean flag = true;  // 不加 volatile
    // private static volatile boolean flag = true;  // 加 volatile 对比

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            System.out.println("t1 开始运行");
            while (flag) {
                // 忙等待
            }
            System.out.println("t1 结束运行");
        });

        t1.start();

        Thread.sleep(2000);
        System.out.println("main 修改 flag = false");
        flag = false;

        t1.join();
        System.out.println("程序结束");
    }
}