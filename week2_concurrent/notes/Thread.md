# 线程生命周期

## 一、六种状态

* NEW
* RUNNABLE
* BLOCKED
* WAITING
* TIMED_WAITING
* TERMINATED

---

## 二、start vs run

| 方法    | 说明   |
| ----- | ---- |
| run   | 普通方法 |
| start | 启动线程 |

---

## 三、状态流转

NEW → RUNNABLE → BLOCKED → WAITING → TERMINATED

---

## 四、注意点

Thread.sleep() 是静态方法，只影响当前线程
