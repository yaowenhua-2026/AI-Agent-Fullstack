# Java 学习笔记 - 第二周：并发编程

## 学习时间
2026年4月1日 - 4月8日

## 学习内容

### 1. 线程生命周期
- 线程6种状态：NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED
- start() vs run() 区别
- sleep() 不释放锁，wait() 释放锁

### 2. Synchronized 与锁
- synchronized 三种用法（实例方法、静态方法、代码块）
- 锁升级：无锁 → 偏向锁 → 轻量级锁 → 重量级锁
- 手写死锁并用 jstack 分析

### 3. volatile 与 CAS
- volatile 保证可见性、禁止指令重排，不保证原子性
- 双重检查锁单例中 volatile 的作用
- CAS 原理：Compare And Swap，CPU 原子指令
- 手写自旋锁

### 4. AQS 源码分析
- AQS 核心结构：state + CLH 队列
- acquire 流程：tryAcquire → addWaiter → acquireQueued
- release 流程：tryRelease → unparkSuccessor
- 公平锁 vs 非公平锁
- 可重入原理：state 计数

### 5. 线程池
- 七大参数：corePoolSize、maximumPoolSize、keepAliveTime、unit、workQueue、threadFactory、handler
- 执行流程：核心 → 队列 → 非核心 → 拒绝
- 容量公式：最大线程数 + 队列容量
- 四种拒绝策略对比

### 6. LeetCode
| 题号 | 题目 | 难度 | 考点 |
|------|------|------|------|
| 2 | 两数相加 | 中等 | 链表遍历 + 进位 |
| 142 | 环形链表 II | 中等 | 快慢指针 + 数学 |
| 160 | 相交链表 | 简单 | 双指针 |
| 104 | 二叉树的最大深度 | 简单 | DFS |
| 226 | 翻转二叉树 | 简单 | 递归 |

## 实验代码
- ThreadStateDemo.java - 线程状态转换演示
- DeadlockDemo.java - 死锁演示
- VisibilityDemo.java - volatile 可见性演示
- SpinLock.java - 手写自旋锁
- ThreadPoolConfigDemo.java - 线程池参数验证

## 关键收获
1. 锁升级是 JVM 优化，从轻到重
2. CAS 是 CPU 原子指令，自旋等待
3. AQS 是 JUC 的基石，用 state + 队列实现同步
4. 线程池用队列缓冲任务，避免线程暴涨
5. 自定义 ThreadPoolExecutor 比 Executors 更安全

## 下周计划
- 线程池拒绝策略详细对比
- 阻塞队列源码分析
- CompletableFuture 异步编程