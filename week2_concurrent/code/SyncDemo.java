package org.example.week2_Threadlife;

public class SyncDemo {

    private int count = 0;

    public  void increment() {
        count++;
    }

    public static void main(String[] args) throws Exception {
        SyncDemo demo = new SyncDemo();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                demo.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                demo.increment();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("count = " + demo.count);  // 应该是 20000
    }
}