package org.example.week2_Threadlife;

import java.util.concurrent.atomic.AtomicReference;

public class SpinLock {

    // 当前持有锁的线程，null 表示没锁
    private AtomicReference<Thread> owner = new AtomicReference<>(null);

    // 加锁
    public void lock() {
        Thread current = Thread.currentThread();
        // CAS 尝试把 owner 从 null 改成当前线程
        while (!owner.compareAndSet(null, current)) {
            // 失败就自旋，空循环
            // 也可以 Thread.yield() 让出 CPU
        }
    }


//3	对比 synchronized 和自旋锁的区别
//4	思考：什么时候用自旋锁？什么时候用 synchronized？

    // 解锁
    public void unlock() {
        Thread current = Thread.currentThread();
        // 只有持有锁的线程才能解锁
        owner.compareAndSet(current, null);
    }
}