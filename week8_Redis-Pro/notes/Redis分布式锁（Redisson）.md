```markdown
# Redis分布式锁（Redisson）- 学习总结

## 学习时间
2026-05-14

---

## 一、分布式锁概述

### 1.1 为什么需要分布式锁？

| 场景 | 问题 | 解决方案 |
|------|------|----------|
| 秒杀系统 | 多个服务实例同时扣库存 | 分布式锁 |
| 定时任务 | 多实例重复执行 | 分布式锁 |
| 防重复提交 | 同一用户短时间内多次请求 | 分布式锁 |

**单机锁（synchronized、ReentrantLock）只能锁住当前 JVM 进程，无法跨实例。**

### 1.2 分布式锁需满足的条件

| 条件 | 说明 |
|------|------|
| 互斥性 | 同一时刻只有一个线程能持有锁 |
| 可重入性 | 同一线程可重复获取锁 |
| 防死锁 | 锁有超时释放机制 |
| 高性能 | 加锁解锁要快 |
| 高可用 | 锁服务不能单点故障 |

---

## 二、Redisson 简介

| 特性 | 说明 |
|------|------|
| 基于 Redis 的 Java 分布式锁框架 | 比手写 `setIfAbsent` 更强大 |
| 支持可重入锁 | 同一线程可重复获取 |
| 支持自动续期（看门狗）| 业务未完成时自动延长过期时间 |
| 支持公平锁、读写锁、红锁 | 功能丰富 |

---

## 三、环境搭建

### 3.1 依赖

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.25.0</version>
</dependency>
3.2 配置类
java
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setConnectionPoolSize(10);
        return Redisson.create(config);
    }
}
四、核心代码实现
4.1 分布式锁（秒杀扣库存）
java
@Service
public class SeckillService {

    @Autowired
    private RedissonClient redissonClient;

    public boolean seckill(Long productId, Long userId) {
        String lockKey = "lock:product:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁：等待3秒，锁超时10秒
            boolean isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return false;   // 没拿到锁
            }

            // 原子扣库存（数据库）
            int rows = productStockMapper.decreaseStock(productId);
            if (rows <= 0) {
                return false;   // 库存不足
            }

            // 创建订单
            createOrder(productId, userId);
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
4.2 可重入锁
java
@GetMapping("/reentrant")
public String testReentrant() {
    RLock lock = redissonClient.getLock("test:reentrant");
    lock.lock();   // 第一次获取
    try {
        // 业务逻辑...
        lock.lock();   // 第二次获取（可重入）
        try {
            // 内部逻辑
        } finally {
            lock.unlock();  // 释放内层锁
        }
    } finally {
        lock.unlock();  // 释放外层锁
    }
    return "ok";
}
Redis 中存储结构（Hash）：

bash
HGETALL test:reentrant
# 输出：
# "uuid:threadId" -> "2"  表示重入次数为 2
4.3 看门狗机制（自动续期）
java
@GetMapping("/watchdog")
public String testWatchdog() {
    RLock lock = redissonClient.getLock("test:watchdog");
    // 不指定 leaseTime，使用默认 30 秒 + 看门狗
    lock.lock();
    try {
        // 模拟长业务（40 秒）
        for (int i = 1; i <= 40; i++) {
            Thread.sleep(1000);
            // 看门狗每 10 秒自动续期
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        lock.unlock();
    }
    return "ok";
}
看门狗原理：

默认锁过期时间 30 秒

每 10 秒检查一次，若当前线程仍持有锁，则自动续期到 30 秒

业务执行完主动 unlock 后，看门狗不再续期

五、测试结果
5.1 秒杀接口（压测）
配置项	值
线程数	50
Ramp-up	1 秒
循环次数	1
路径	POST /seckill/1
5.2 可重入锁测试
bash
curl http://localhost:8080/seckill/reentrant
控制台输出：

text
========== 外层获取锁 ==========
外层锁获取成功
内层锁获取成功（可重入）
内层锁释放
外层锁释放
六、Redisson vs Redis 原生锁
对比项	Redis 原生（setIfAbsent）	Redisson
可重入	❌ 需手动实现	✅ 内置支持
自动续期	❌ 需手动实现	✅ 看门狗
公平锁	❌	✅
读写锁	❌	✅
代码复杂度	高	低
七、面试速答
问题	答案
分布式锁用在哪些场景？	秒杀、定时任务、防重复提交
Redisson 锁的特点？	可重入、自动续期、支持公平锁/读写锁
看门狗机制是什么？	锁未释放时自动延长过期时间，默认每 10 秒续期一次
如何避免死锁？	tryLock 设置超时时间 + finally 中释放锁
可重入锁如何实现？	使用 Hash 结构，key 存储客户端标识，value 存储重入次数
八、一句话总结
Redisson 分布式锁通过可重入锁、看门狗自动续期、公平锁/读写锁等特性，为高并发场景提供了可靠、易用的分布式互斥方案。