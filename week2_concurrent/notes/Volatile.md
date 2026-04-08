# volatile 与内存模型

## 一、作用

* 可见性
* 禁止指令重排

---

## 二、volatile vs synchronized

| 特性  | volatile | synchronized |
| --- | -------- | ------------ |
| 原子性 | ❌        | ✅            |
| 可见性 | ✅        | ✅            |

---

## 三、双重检查锁

```java
private static volatile Singleton instance;
```

---

## 四、CAS 回顾

* 原子操作
* 自旋重试
