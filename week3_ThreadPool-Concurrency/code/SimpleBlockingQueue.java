package org.example.week3_threadpool;

import java.util.LinkedList;

/**
 * 自定义阻塞队列
 * 用 wait/notify 实现
 */
public class SimpleBlockingQueue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final int capacity;

    public SimpleBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 放入元素（队列满则阻塞）
     */
    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) {
            System.out.println(Thread.currentThread().getName() + " 队列满，等待...");
            wait();  // 阻塞，释放锁
        }
        queue.add(item);
        System.out.println(Thread.currentThread().getName() + " 放入: " + item + "，队列大小: " + queue.size());
        notifyAll();  // 唤醒等待的消费者
    }

    /**
     * 取出元素（队列空则阻塞）
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + " 队列空，等待...");
            wait();  // 阻塞，释放锁
        }
        T item = queue.removeFirst();
        System.out.println(Thread.currentThread().getName() + " 取出: " + item + "，队列大小: " + queue.size());
        notifyAll();  // 唤醒等待的生产者
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }

    // ========== 测试代码 ==========
    public static void main(String[] args) {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(2);

        // 生产者线程（生产 5 个）
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "生产者");

        // 消费者线程（消费 5 个，速度慢）
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.take();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "消费者");

        producer.start();
        consumer.start();
    }
}
