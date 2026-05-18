# 本周学习总结：Redis 高级 + 秒杀系统

## 学习时间
2026-05-13 ~ 2026-05-19

---

## 一、学习概览

| 日期 | 学习内容 | 产出 |
|------|----------|------|
| 周一 | 缓存穿透、击穿、雪崩 | 三种场景解决方案 |
| 周二 | Redis 分布式锁（Redisson）| 分布式锁 demo |
| 周三 | 缓存+数据库一致性 | 方案对比文档 |
| 周四 | 秒杀系统实战 | 预热、限流、下单模块 |
| 周五 | 阶段复习 | 知识体系图、笔记 |
| 周六-周日 | 模拟面试 | MySQL+Redis 面试题笔记 |

---

## 二、笔记目录
redis-advanced/
├── README.md # 总览（本文件）
├── 01-缓存穿透击穿雪崩.md
├── 02-Redis分布式锁.md
├── 03-缓存与数据库一致性.md
├── 04-秒杀系统实战.md
├── 05-阶段复习笔记.md
└── 06-模拟面试题.md

text

---

## 三、核心知识点速查

### 3.1 缓存三大问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| **缓存穿透** | 查询不存在的数据 | 布隆过滤器、缓存空值 |
| **缓存击穿** | 热点 key 过期 | 互斥锁、逻辑过期 |
| **缓存雪崩** | 大量 key 同时过期 | 随机过期时间、缓存预热 |

### 3.2 分布式锁（Redisson）

| 特性 | 说明 |
|------|------|
| 可重入锁 | 同一线程可重复获取，Hash 结构存储重入次数 |
| 看门狗 | 锁未释放时自动续期，默认 30 秒/10 秒 |
| 公平锁 | FIFO 顺序获取锁 |
| 读写锁 | 读共享，写独占 |

### 3.3 缓存一致性方案

| 方案 | 核心流程 | 适用场景 |
|------|----------|----------|
| **Cache-Aside** | 先更新 DB，再删缓存 | **99% 场景** |
| **延迟双删** | 删→等→再删 | 一致性要求稍高 |
| **双写+MQ** | 更新 DB→MQ→缓存 | 异步解耦 |
| **Canal** | 监听 binlog→更新缓存 | 零侵入，异构同步 |

### 3.4 秒杀系统核心优化

| 优化点 | 实现 | 效果 |
|--------|------|------|
| 分段锁 | `userId % 16` | 并发度提高 16 倍 |
| 预检库存（锁外）| 先读 Redis | 减少无效锁争抢 |
| 锁内只做扣减 | 只做 `decrement` | 锁持有时间降至几毫秒 |
| 防重复提交 | `setIfAbsent` | 防止恶意刷单 |
| 异步下单 | RocketMQ | 减轻 DB 压力 |

---

## 四、压测结果

| 指标 | 优化前 | 优化后 |
|------|--------|--------|
| 平均响应时间 | 10~25 秒 | **1.2 秒** |
| 错误率 | > 90% | **0%** |
| 库存准确度 | 不准确 | **准确** |
| 并发处理能力 | 串行 | **并行（16段）** |

**结论**：500 并发，300 库存全部售罄，无超卖 ✅

---

## 五、面试速答卡

| 问题 | 答案 |
|------|------|
| 缓存穿透怎么解决？ | 布隆过滤器、缓存空值 |
| 缓存击穿怎么解决？ | 互斥锁、逻辑过期 |
| 缓存雪崩怎么解决？ | 随机过期时间、缓存预热 |
| Redisson 锁特点？ | 可重入、看门狗自动续期 |
| 如何防超卖？ | Redis 原子扣减 + 数据库乐观锁 |
| 分段锁作用？ | 将一把锁变成多把锁，提高并发度 |
| Cache-Aside 是什么？ | 先更新 DB，再删缓存 |
| Canal 是什么？ | 监听 binlog，零侵入更新缓存 |

---

## 六、代码示例

### 分段锁初始化

```java
private static final int SEGMENT_COUNT = 16;
private RLock[] segmentLocks;

@PostConstruct
public void init() {
    segmentLocks = new RLock[SEGMENT_COUNT];
    for (int i = 0; i < SEGMENT_COUNT; i++) {
        segmentLocks[i] = redissonClient.getLock("lock:product:segment:" + i);
    }
}

int segmentId = (int) (userId % SEGMENT_COUNT);
RLock lock = segmentLocks[segmentId];
防重复提交
java
private boolean checkUserToken(Long userId, Long productId) {
    String tokenKey = "seckill:token:" + userId + ":" + productId;
    Boolean success = redisTemplate.opsForValue()
        .setIfAbsent(tokenKey, "1", 10, TimeUnit.SECONDS);
    return Boolean.TRUE.equals(success);
}
原子扣减 Redis 库存
java
Long newStock = redisTemplate.opsForValue().decrement("product:stock:" + productId);
if (newStock < 0) {
    redisTemplate.opsForValue().increment("product:stock:" + productId);
    return "已售罄";
}
七、完成确认
模块	状态
缓存穿透、击穿、雪崩	✅
Redis 分布式锁（Redisson）	✅
缓存+数据库一致性	✅
秒杀系统实战	✅
阶段复习笔记	✅
模拟面试题	✅
本周学习完成！