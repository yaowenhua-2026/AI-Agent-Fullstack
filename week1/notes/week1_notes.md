# Week1 Notes: JVM 内存模型与垃圾回收

## 1. JVM 内存结构
- 程序计数器、JVM栈、本地方法栈、堆、方法区/元空间
- 实践：jstat 查看内存变化，绘制内存结构图

## 2. 对象分配过程
- TLAB、Eden、Survivor(S0/S1)
- 实践：写 Java 代码创建对象，jmap 查看分配情况

## 3. 垃圾回收算法
- 标记-清除、复制、标记-整理
- 实践：模拟内存溢出，分析 GC 日志

## 4. 垃圾回收器对比
- G1、ZGC
- 实践：配置不同 GC 参数对比，记录调优笔记

## 5. 类加载机制
- 双亲委派模型
- 实践：自定义 ClassLoader 加载加密类

## 6. LeetCode 实践（本周 5 题）
| 文件 | 题目 | 核心思路 |
|------|------|-----------|
| hasCycle1.java | 链表环判断 | 快慢指针，Floyd 判圈算法 |
| twoSum.java | 两数之和 | 哈希表存值 |
| reverseLinkedList.java | 反转链表 | 迭代 & 递归 |
| maxSubArray.java | 最大子数组和 | 动态规划 |
| climbStairs.java | 爬楼梯 | 动态规划 / Fibonacci |

- 每题上传单独 Java 文件到 `code/` 文件夹

## 7. 学习收获
- 能画出 JVM 内存结构图
- 理解对象分配和 GC 原理
- 熟悉 ClassLoader 自定义实现
- 完成 5 道 LeetCode 题目

## 8. 遇到的难点
- 对象分配和垃圾回收过程理解抽象
- ClassLoader 父类委派机制需注意

## 9. 后续改进
- 增加 LeetCode 练习题
- GC 日志分析进一步细化
- 可绘制更详细流程图辅助理解