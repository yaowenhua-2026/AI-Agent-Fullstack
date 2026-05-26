### 4. Bean生命周期.md

```markdown
# Bean 生命周期

## 学习时间
2026-05-23

---

## 一、Bean 生命周期流程图
┌─────────────────────────────────────────────────────────────────────────────┐
│ Spring Bean 生命周期 │
├─────────────────────────────────────────────────────────────────────────────┤
│ │
│ 1. 实例化（构造方法） │
│ ↓ │
│ 2. 属性注入（setter、@Autowired） │
│ ↓ │
│ 3. BeanNameAware（setBeanName） │
│ ↓ │
│ 4. BeanFactoryAware（setBeanFactory） │
│ ↓ │
│ 5. ApplicationContextAware（setApplicationContext） │
│ ↓ │
│ 6. BeanPostProcessor.postProcessBeforeInitialization() ⭐ │
│ ↓ │
│ 7. @PostConstruct │
│ ↓ │
│ 8. InitializingBean.afterPropertiesSet() │
│ ↓ │
│ 9. 自定义 init-method │
│ ↓ │
│ 10. BeanPostProcessor.postProcessAfterInitialization() ⭐ │
│ ↓ │
│ 11. Bean 就绪（业务方法） │
│ ↓ │
│ 12. @PreDestroy │
│ ↓ │
│ 13. DisposableBean.destroy() │
│ ↓ │
│ 14. 自定义 destroy-method │
│ │
└─────────────────────────────────────────────────────────────────────────────┘

text

---

## 二、核心接口对比

### 2.1 Aware 接口（感知接口）

| 接口 | 注入内容 | 执行时机 |
|------|----------|----------|
| `BeanNameAware` | Bean 的名称 | 属性注入后 |
| `BeanFactoryAware` | BeanFactory | 属性注入后 |
| `ApplicationContextAware` | ApplicationContext | 属性注入后 |
| `EnvironmentAware` | Environment 配置 | 属性注入后 |

### 2.2 初始化方式对比

| 方式 | 执行顺序 | 说明 |
|------|----------|------|
| `@PostConstruct` | 1 | JSR-250 标准注解，推荐 |
| `InitializingBean.afterPropertiesSet()` | 2 | Spring 接口，耦合度高 |
| `@Bean(initMethod="xxx")` | 3 | XML 或 Java 配置 |

### 2.3 销毁方式对比

| 方式 | 执行顺序 | 说明 |
|------|----------|------|
| `@PreDestroy` | 1 | JSR-250 标准注解，推荐 |
| `DisposableBean.destroy()` | 2 | Spring 接口，耦合度高 |
| `@Bean(destroyMethod="xxx")` | 3 | XML 或 Java 配置 |
