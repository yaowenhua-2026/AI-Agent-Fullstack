# 自动配置原理与自定义 Starter

## 学习时间
2026-05-21

---

## 一、自动配置原理

### 1.1 核心注解

```java
@SpringBootApplication
    └── @EnableAutoConfiguration  // ⭐ 核心
        └── @Import(AutoConfigurationImportSelector.class)
            └── 读取 META-INF/spring/.../AutoConfiguration.imports
                └── 加载所有自动配置类
1.2 配置文件位置（Spring Boot 2.7+）
text
src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
文件内容：

text
com.example.hello.HelloAutoConfiguration
二、三大核心组件
组件	作用
HelloProperties	绑定配置文件（@ConfigurationProperties）
HelloService	核心业务逻辑
HelloAutoConfiguration	条件判断 + 创建 Bean（@Bean）
三、条件注解
注解	作用
@ConditionalOnClass	classpath 存在指定类时生效
@ConditionalOnMissingBean	容器中不存在指定 Bean 时创建（允许用户覆盖）
@ConditionalOnProperty	配置文件存在指定属性时生效（开关控制）
开关控制示例
java
@ConditionalOnProperty(prefix = "hello", name = "enabled", havingValue = "true", matchIfMissing = true)
用户关闭 Starter：

yaml
hello:
  enabled: false
四、@ConfigurationProperties vs @Value
注解	作用	适用场景
@ConfigurationProperties	批量绑定多个属性	hello.prefix、hello.suffix
@Value	单个属性注入	@Value("${server.port}")
五、完整流程图
text
用户项目启动
    ↓
@EnableAutoConfiguration
    ↓
读取 META-INF/spring/.../AutoConfiguration.imports
    ↓
加载 HelloAutoConfiguration
    ↓
检查 @ConditionalOnProperty（开关是否开启）
    ↓ 开启
检查 @ConditionalOnMissingBean（用户是否自己创建了）
    ↓ 没有
执行 @Bean 方法，创建 HelloService
    ↓
用户项目 @Autowired HelloService → 注入成功
六、项目结构
text
hello-spring-boot-starter/
├── pom.xml
└── src/main/java/com/example/hello/
    ├── HelloAutoConfiguration.java   ← 自动配置
    ├── HelloProperties.java          ← 配置绑定
    └── HelloService.java             ← 核心服务
└── src/main/resources/META-INF/spring/
    └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
七、面试速答
问题	答案
自动配置核心注解？	@EnableAutoConfiguration
自动配置类在哪注册？	META-INF/spring/.../AutoConfiguration.imports
如何让用户覆盖默认实现？	@ConditionalOnMissingBean
如何让用户控制开关？	@ConditionalOnProperty
@ConfigurationProperties 作用？	批量绑定配置
Starter 三大组件？	Properties、Service、AutoConfiguration