package org.example.week2_Threadlife;

public class DeadlockDemo {

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("t1 获得 lockA");
                try { Thread.sleep(100); } catch (Exception e) {}
                synchronized (lockB) {
                    System.out.println("t1 获得 lockB");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("t2 获得 lockB");
                try { Thread.sleep(100); } catch (Exception e) {}
                synchronized (lockA) {
                    System.out.println("t2 获得 lockA");
                }
            }
        });

        t1.start();
        t2.start();
    }
}