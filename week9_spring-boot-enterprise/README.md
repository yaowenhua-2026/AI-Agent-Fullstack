# 本周学习总结：Spring 核心进阶

## 学习时间
2026-05-20 ~ 2026-05-26

---

## 一、学习概览

| 日期 | 学习内容 | 产出 |
|------|----------|------|
| 周一 | Spring Boot 启动流程 | 启动流程图、12个步骤分析 |
| 周二 | 自动配置原理 & 自定义 Starter | 手写 Hello Starter |
| 周三 | 条件注解 @Conditional | 三大核心条件注解 |
| 周四 | Bean 生命周期 & BeanPostProcessor | Bean 监控 demo |
| 周五 | Spring 事件机制 | 用户注册事件监听 |
| 周六 | LeetCode 图算法（2道）| 岛屿数量、腐烂的橘子 |

---

## 二、笔记目录
spring-core-advanced/
├── README.md # 总览（本文件）
├── 01-Spring-Boot启动流程.md
├── 02-自动配置原理与自定义Starter.md
├── 03-条件注解.md
├── 04-Bean生命周期.md
├── 05-Spring事件机制.md
└── 06-LeetCode图算法.md

text

---

## 三、核心知识点速查

### 3.1 Spring Boot 启动流程（6步）
创建实例 → run() → 准备环境 → 创建容器 → 刷新容器(12步) → 执行Runner

text

### 3.2 自动配置三大组件

| 组件 | 作用 |
|------|------|
| HelloProperties | 绑定配置文件 |
| HelloService | 核心业务逻辑 |
| HelloAutoConfiguration | 条件判断 + 创建 Bean |

### 3.3 三大核心条件注解

| 注解 | 作用 | 记忆口诀 |
|------|------|----------|
| `@ConditionalOnMissingBean` | 容器中没有 Bean 时才创建 | "你不来，我来" |
| `@ConditionalOnProperty` | 配置文件属性匹配时才生效 | "听配置的" |
| `@ConditionalOnClass` | 类路径存在指定类时才生效 | "有你才行" |

### 3.4 Bean 生命周期（14个阶段）
实例化 → 属性注入 → Aware接口 → BeanPostProcessor前置 → 初始化(3种) → BeanPostProcessor后置 → 使用 → 销毁(3种)

text

### 3.5 Spring 事件机制
事件(Event) → 发布器(Publisher) → 广播器(Multicaster) → 监听器(Listener)

text

### 3.6 LeetCode 图算法

| 题目 | 核心算法 |
|------|----------|
| 200. 岛屿数量 | DFS/BFS 遍历 |
| 994. 腐烂的橘子 | 多源 BFS |

---

## 四、面试速答卡

| 问题 | 答案 |
|------|------|
| Spring Boot 启动流程？ | 6步：创建实例→run→环境→容器→刷新→Runner |
| refresh() 做了哪几件重要的事？ | 处理 PostProcessor、启动 Web 服务器、实例化 Bean |
| 如何写一个 Starter？ | 三大组件 + AutoConfiguration.imports |
| `@ConditionalOnMissingBean` 作用？ | 允许用户覆盖默认实现 |
| BeanPostProcessor 作用？ | 在 Bean 初始化前后插入自定义逻辑 |
| Spring 事件机制是什么？ | 观察者模式，解耦业务逻辑 |

---

## 五、代码示例

### 5.1 自定义 Starter 自动配置类

```java
@Configuration
@EnableConfigurationProperties(HelloProperties.class)
@ConditionalOnClass(HelloService.class)
public class HelloAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public HelloService helloService(HelloProperties properties) {
        return new HelloService(properties.getPrefix(), properties.getSuffix());
    }
}
5.2 条件注解开关控制
java
@ConditionalOnProperty(prefix = "monitor", name = "enabled", 
                       havingValue = "true", matchIfMissing = false)
5.3 BeanPostProcessor 监控
java
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前: " + beanName);
        return bean;
    }
}
5.4 事件监听
java
@Component
public class UserRegisterListener {
    @EventListener
    public void handleSendEmail(UserRegisterEvent event) {
        System.out.println("发送邮件给: " + event.getEmail());
    }
}
六、完成确认
模块	状态
Spring Boot 启动流程	✅
自动配置与 Starter	✅
条件注解	✅
Bean 生命周期	✅
Spring 事件机制	✅
LeetCode 图算法（2道）	✅