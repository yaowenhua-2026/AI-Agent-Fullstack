# CAS 与自旋锁

## 一、CAS 是什么？

Compare And Swap（比较并交换）

```java
CAS(地址, 期望值, 新值)
```

---

## 二、AtomicInteger 原理

```java
for (;;) {
    if (compareAndSet(current, next)) {
        return next;
    }
}
```

---

## 三、自旋锁实现

```java
while (!owner.compareAndSet(null, current)) {}
```

---

## 四、优缺点

| 优点 | 缺点     |
| -- | ------ |
| 无锁 | 占 CPU  |
| 快  | ABA 问题 |

---

## 五、锁升级

无锁 → 偏向锁 → 轻量级锁 → 重量级锁
