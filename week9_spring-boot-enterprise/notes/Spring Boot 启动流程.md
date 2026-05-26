### 1. Spring Boot 启动流程.md

```markdown
# Spring Boot 启动流程

## 学习时间
2026-05-20

---

## 一、整体流程图
┌─────────────────────────────────────────────────────────────────────────────┐
│ Spring Boot 启动流程 │
├─────────────────────────────────────────────────────────────────────────────┤
│ │
│ SpringApplication.run() │
│ │ │
│ ▼ │
│ ┌─────────────────────────────────────────────────────────────────────┐ │
│ │ 第一步：创建 SpringApplication 实例 │ │
│ │ • 推断 Web 应用类型（Servlet/Reactive/None） │ │
│ │ • 加载 ApplicationContextInitializer（初始化器） │ │
│ │ • 加载 ApplicationListener（监听器） │ │
│ │ • 推断主配置类（@SpringBootApplication 所在类） │ │
│ └─────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌─────────────────────────────────────────────────────────────────────┐ │
│ │ 第二步：运行 run() 方法 │ │
│ │ • 启动计时器（StopWatch） │ │
│ │ • 创建 BootstrapContext（临时储物柜） │ │
│ │ • 加载 SpringApplicationRunListeners（启动监听器） │ │
│ │ • 发布 starting 事件 │ │
│ └─────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌─────────────────────────────────────────────────────────────────────┐ │
│ │ 第三步：准备环境 Environment │ │
│ │ • 加载配置文件（application.yml/properties） │ │
│ │ • 处理命令行参数 │ │
│ │ • 激活 Profiles │ │
│ │ • 发布 environmentPrepared 事件 │ │
│ │ • 打印 Banner │ │
│ └─────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌─────────────────────────────────────────────────────────────────────┐ │
│ │ 第四步：创建 ApplicationContext（IoC 容器） │ │
│ │ • 根据 Web 类型创建对应容器 │ │
│ │ • Servlet → AnnotationConfigServletWebServerApplicationContext │ │
│ │ • Reactive → AnnotationConfigReactiveWebServerApplicationContext │ │
│ │ • None → AnnotationConfigApplicationContext │ │
│ └─────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌─────────────────────────────────────────────────────────────────────┐ │
│ │ 第五步：刷新容器 refresh() ⭐⭐⭐ 核心 │ │
│ │ • 12 个步骤，详见下方 │ │
│ │ • 处理 Bean 定义、实例化 Bean、启动 Web 服务器 │ │
│ └─────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌─────────────────────────────────────────────────────────────────────┐ │
│ │ 第六步：执行 Runner │ │
│ │ • CommandLineRunner │ │
│ │ • ApplicationRunner │ │
│ └─────────────────────────────────────────────────────────────────────┘ │
│ │
└─────────────────────────────────────────────────────────────────────────────┘

text

---

## 二、refresh() 12 个步骤（核心）
┌─────────────────────────────────────────────────────────────────────────────┐
│ refresh() 12 个步骤 │
├─────────────────────────────────────────────────────────────────────────────┤
│ │
│ 1️⃣ prepareRefresh（准备刷新） │
│ └── 记录启动时间、设置容器状态、初始化属性源、校验环境 │
│ ↓ │
│ 2️⃣ obtainFreshBeanFactory（获取 BeanFactory） │
│ └── 创建或获取 BeanFactory，加载 Bean 定义 │
│ ↓ │
│ 3️⃣ prepareBeanFactory（准备 BeanFactory） │
│ └── 设置类加载器、注册特殊 Bean、添加 BeanPostProcessor │
│ ↓ │
│ 4️⃣ postProcessBeanFactory（子类后置处理） │
│ └── 给子类扩展点，允许添加更多 BeanPostProcessor │
│ ↓ │
│ 5️⃣ invokeBeanFactoryPostProcessors（处理 Bean 定义）⭐ │
│ └── 执行 BeanFactoryPostProcessor，处理 @Configuration、@ComponentScan │
│ ↓ │
│ 6️⃣ registerBeanPostProcessors（注册 Bean 创建器）⭐ │
│ └── 注册 BeanPostProcessor，准备处理 @Autowired、@Value │
│ ↓ │
│ 7️⃣ initMessageSource（国际化） │
│ └── 初始化消息源，支持 i18n │
│ ↓ │
│ 8️⃣ initApplicationEventMulticaster（事件广播器） │
│ └── 初始化事件广播器，用于容器内事件通信 │
│ ↓ │
│ 9️⃣ onRefresh（刷新子类）⭐ │
│ └── 启动内嵌 Web 服务器（Tomcat/Jetty/Undertow） │
│ ↓ │
│ 🔟 registerListeners（注册监听器） │
│ └── 注册事件监听器，订阅容器事件 │
│ ↓ │
│ 1️⃣1️⃣ finishBeanFactoryInitialization（实例化单例 Bean）⭐ │
│ └── 实例化所有非懒加载的单例 Bean │
│ ↓ │
│ 1️⃣2️⃣ finishRefresh（完成刷新） │
│ └── 清理缓存、初始化生命周期处理器、发布 ContextRefreshedEvent │
│ │
└─────────────────────────────────────────────────────────────────────────────┘

text

---

## 三、关键概念详解

### 3.1 Web 应用类型

| 类型 | 说明 | 创建的容器 | 启动的服务器 |
|------|------|-----------|-------------|
| `SERVLET` | 传统 Web 应用 | `AnnotationConfigServletWebServerApplicationContext` | Tomcat/Jetty |
| `REACTIVE` | 响应式 Web | `AnnotationConfigReactiveWebServerApplicationContext` | Netty |
| `NONE` | 普通 Java 应用 | `AnnotationConfigApplicationContext` | 无 |

### 3.2 ApplicationContext vs BeanFactory

| 特性 | BeanFactory | ApplicationContext |
|------|-------------|-------------------|
| Bean 管理 | ✅ | ✅ |
| 国际化 | ❌ | ✅ |
| 事件发布 | ❌ | ✅ |
| 环境抽象 | ❌ | ✅ |
| AOP 支持 | ❌ | ✅ |

**一句话**：`ApplicationContext` = `BeanFactory` + 企业级功能

### 3.3 两种监听器区别

| 类型 | 监听对象 | 触发时机 |
|------|----------|----------|
| `SpringApplicationRunListener` | Spring Boot **启动流程** | starting、started、ready |
| `ApplicationListener` | Spring **容器事件** | ContextRefreshedEvent 等 |

### 3.4 两种 PostProcessor 区别

| 类型 | 作用 | 执行时机 | 举例 |
|------|------|----------|------|
| `BeanFactoryPostProcessor` | 修改 **Bean 定义** | 实例化之前 | `@Configuration` 处理 |
| `BeanPostProcessor` | 修改 **Bean 实例** | 实例化前后 | `@Autowired` 处理 |

### 3.5 配置优先级
命令行参数（最高）
↓
系统属性（-D 参数）
↓
环境变量
↓
application.yml
↓
application.properties
↓
默认值（最低）

text

---

## 四、事件发布顺序
starting
↓
environmentPrepared
↓
contextPrepared
↓
contextLoaded
↓
started
↓
ready
↓
（失败时）failed

text

---

## 五、扩展点汇总

| 扩展点 | 作用 | 使用方式 |
|--------|------|----------|
| `ApplicationContextInitializer` | 容器创建后、刷新前定制 | `spring.factories` |
| `ApplicationListener` | 监听容器事件 | `spring.factories` 或 `@EventListener` |
| `SpringApplicationRunListener` | 监听启动流程 | `spring.factories` |
| `CommandLineRunner` | 启动后执行 | `@Component` |
| `ApplicationRunner` | 启动后执行（有参数）| `@Component` |

---

## 六、面试速答

| 问题 | 答案 |
|------|------|
| Spring Boot 启动流程核心步骤？ | 创建实例 → run → 环境 → 容器 → 刷新 → Runner |
| 为什么推断 Web 类型？ | 决定用哪个容器和 Web 服务器 |
| refresh() 做了哪几件重要的事？ | 处理 PostProcessor、启动 Web 服务器、实例化 Bean |
| Web 服务器什么时候启动？ | `onRefresh()` 步骤 |
| 两种监听器区别？ | 一个监听启动流程，一个监听容器事件 |
| PostProcessor 区别？ | BeanFactoryPostProcessor 改定义，BeanPostProcessor 改实例 |
| 配置优先级？ | 命令行 > 系统属性 > 环境变量 > yml |
| 启动后执行代码怎么写？ | 实现 `ApplicationRunner` 或 `CommandLineRunner` |