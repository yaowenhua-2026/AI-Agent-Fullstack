# BlockingQueue 阻塞队列学习总结

## 一、BlockingQueue 是什么？
**阻塞队列 = 线程安全的队列 + 阻塞操作**

| 操作 | 队列空 | 队列满 |
|------|--------|--------|
| put(e) | 放入成功 | 阻塞 |
| take() | 阻塞 | 取出成功 |

**解决的问题**
- 生产者-消费者模式中，避免轮询浪费 CPU
- 自动处理线程阻塞和唤醒

## 二、核心方法

| 类型 | 方法 | 行为 |
|------|------|------|
| 阻塞 | `put(e)`、`take()` | 满/空时阻塞 |
| 非阻塞 | `offer(e)`、`poll()` | 返回 false/null |
| 超时 | `offer(e, time, unit)` | 阻塞指定时间 |

## 三、实现原理（wait/notify）

```java
public synchronized void put(T item) throws InterruptedException {
    while (queue.size() == capacity) {
        wait();
    }
    queue.add(item);
    notifyAll();
}

public synchronized T take() throws InterruptedException {
    while (queue.isEmpty()) {
        wait();
    }
    T item = queue.removeFirst();
    notifyAll();
    return item;
}
关键点

synchronized 保证线程安全
wait() 阻塞并释放锁
notifyAll() 唤醒所有线程
while 防止虚假唤醒
四、常见实现
队列	有界	锁	数据结构
ArrayBlockingQueue	有界	单锁	数组
LinkedBlockingQueue	可选	双锁	链表
SynchronousQueue	容量0	无	直接交换
PriorityBlockingQueue	无界	单锁	堆
五、线程池中的应用

提交任务 → 核心线程忙 → 进入队列 → 队列满 → 创建非核心线程 → 队列空消费者阻塞

六、关键知识点
生产者消费者模型
put/take 阻塞机制
while 防止虚假唤醒
队列实现差异
线程池中的任务队列