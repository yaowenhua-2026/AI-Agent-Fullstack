# AQS 源码分析笔记

## 一、AQS 是什么？

AQS（AbstractQueuedSynchronizer）是 Java 并发包的核心组件。

常见实现：

* ReentrantLock
* Semaphore
* CountDownLatch

---

## 二、核心结构

```java
private volatile int state;
private transient volatile Node head;
private transient volatile Node tail;
```

* `state`：同步状态
* `head/tail`：双向队列

---

## 三、获取锁流程

1. tryAcquire（尝试获取锁）
2. addWaiter（入队）
3. acquireQueued（阻塞等待）

---

## 四、释放锁流程

```java
release → tryRelease → unparkSuccessor
```

---

## 五、公平锁 vs 非公平锁

| 维度  | 公平锁 | 非公平锁 |
| --- | --- | ---- |
| 抢锁  | 排队  | 抢占   |
| 吞吐量 | 低   | 高    |
| 默认  | ❌   | ✅    |

---

## 六、可重入原理

state 递增表示重入次数

---

## 七、核心总结

* AQS = 队列 + CAS + state
* 只有 head.next 才能竞争锁
