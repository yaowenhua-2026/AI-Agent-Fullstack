

## 一、是什么
线程本地变量，每个线程独立副本

## 二、结构
Thread → ThreadLocalMap → Entry(key, value)

- key：弱引用
- value：强引用

## 三、为什么用弱引用
避免 ThreadLocal 无法回收

## 四、内存泄漏原因
- key 被 GC
- value 仍存在
- 线程池线程不销毁
→ 泄漏

## 五、正确用法

```java
ThreadLocal<BigObject> tl = new ThreadLocal<>();
try {
    tl.set(new BigObject());
} finally {
    tl.remove();
}
六、线程池风险

线程复用 → 更容易泄漏

七、面试速记
原理：ThreadLocalMap
泄漏原因：key=null，value还在
解决：remove()