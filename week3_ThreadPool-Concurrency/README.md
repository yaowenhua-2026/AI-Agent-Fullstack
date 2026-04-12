# 本周学习总结

这周主要学习了 Java 并发相关的几个核心知识点，围绕线程池、阻塞队列、异步编排以及 ThreadLocal 内存泄漏进行了系统梳理。

## 本周学习内容

### 1. BlockingQueue 阻塞队列
学习了 BlockingQueue 的基本概念，理解了它本质上是“线程安全的队列 + 阻塞操作”。  
重点掌握了：
- `put()`、`take()` 的阻塞特性
- `offer()`、`poll()` 的非阻塞特性
- 使用 `wait/notify` 实现一个简单阻塞队列
- `while` 防止虚假唤醒的原因
- BlockingQueue 在线程池中的作用

---

### 2. 线程池核心参数与执行流程
学习了 ThreadPoolExecutor 的七大参数，以及任务提交到线程池后的完整执行流程。  
重点掌握了：
- `corePoolSize`
- `maximumPoolSize`
- `keepAliveTime`
- `workQueue`
- `threadFactory`
- `handler`

同时也理解了线程池执行流程：
1. 先看核心线程是否已满  
2. 未满则创建核心线程  
3. 已满则尝试进入阻塞队列  
4. 队列满了再创建非核心线程  
5. 非核心线程也满了则触发拒绝策略  

---

### 3. 四种拒绝策略
学习了线程池满载时的四种拒绝策略，并了解了它们各自的适用场景。

包括：
- `AbortPolicy`
- `CallerRunsPolicy`
- `DiscardPolicy`
- `DiscardOldestPolicy`

也进一步理解了不同业务场景下如何选择合适的拒绝策略，比如：
- 核心业务适合 `AbortPolicy`
- 日志类任务适合 `DiscardPolicy`
- 实时性要求高的场景适合 `DiscardOldestPolicy`

---

### 4. CompletableFuture 异步编排
学习了 CompletableFuture 的基本使用方式，理解了它相比传统 Future 的优势。

重点掌握了：
- `supplyAsync()`
- `runAsync()`
- 异步任务的创建方式
- CompletableFuture 支持链式调用、任务组合和异常处理

对异步编排的理解更加清晰了，也认识到它在提高程序并发执行能力方面非常实用。

---

### 5. ThreadLocal 内存泄漏
学习了 ThreadLocal 的底层原理，以及为什么在线程池场景下容易发生内存泄漏。

重点掌握了：
- ThreadLocalMap 的结构
- Entry 中 key 是弱引用，value 是强引用
- key 被回收后 value 仍可能残留
- 线程池中的线程长时间存活会放大泄漏风险
- 正确使用方式是 `try-finally` 中调用 `remove()`

示例：

```java
ThreadLocal<BigObject> tl = new ThreadLocal<>();
try {
    tl.set(new BigObject());
} finally {
    tl.remove();
}
本周收获

通过这一周的学习，我对 Java 并发编程中的几个高频知识点有了更清晰的认识：

理解了阻塞队列的作用和实现原理
掌握了线程池的核心参数和执行流程
熟悉了四种拒绝策略及其适用场景
初步掌握了 CompletableFuture 的异步编排能力
理解了 ThreadLocal 内存泄漏的原因和规避方式