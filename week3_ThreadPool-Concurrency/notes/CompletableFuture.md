# CompletableFuture 异步编排学习总结

## 一、为什么用 CompletableFuture？

| 问题 | Future | CompletableFuture |
|------|--------|------------------|
| 获取结果 | 阻塞 | 非阻塞 |
| 链式调用 | ❌ | ✅ |
| 组合任务 | ❌ | ✅ |
| 异常处理 | 复杂 | 简单 |

## 二、创建任务

| 方法 | 返回值 | 场景 |
|------|--------|------|
| supplyAsync | 有返回值 | 计算 |
| runAsync | 无返回值 | 执行 |

```java
CompletableFuture.supplyAsync(() -> "结果");
CompletableFuture.runAsync(() -> System.out.println("执行"));