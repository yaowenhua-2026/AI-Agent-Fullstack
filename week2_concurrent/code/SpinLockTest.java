package org.example.week2_Threadlife;

public class SpinLockTest {

    private static int count = 0;
    private static SpinLock lock = new SpinLock();

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                lock.lock();
                count++;
                lock.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                lock.lock();
                count++;
                lock.unlock();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("count = " + count);  // 应该是 20000
    }
}