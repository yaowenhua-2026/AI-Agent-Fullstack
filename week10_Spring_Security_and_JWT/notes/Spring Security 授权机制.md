# Spring Security 授权机制

## 学习时间
2026-05-28

---

## 一、核心概念

| 概念 | 说明 |
|------|------|
| **认证（Authentication）** | 确认用户身份（登录） |
| **授权（Authorization）** | 决定用户能做什么（权限检查） |

Spring Security 授权基于 **RBAC（Role-Based Access Control）** 模型，通过角色和权限控制资源访问。

---

## 二、核心组件

| 组件 | 作用 |
|------|------|
| `GrantedAuthority` | 权限标识（如 `ROLE_ADMIN`、`order:read`） |
| `UserDetails` | 用户信息，包含用户名、密码、权限集合 |
| `SecurityContextHolder` | 存储当前认证用户信息 |
| `@PreAuthorize` | 方法执行前检查权限 |
| `@PostAuthorize` | 方法执行后检查权限 |
| `@PostFilter` | 方法执行后过滤返回值 |
| `HttpSecurity` | URL 级别权限配置 |

---

## 三、基于 URL 的权限控制

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.defaultSuccessUrl("/home"))
            .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }
}
常用方法
方法	作用
permitAll()	所有人可访问
denyAll()	所有人不可访问
authenticated()	登录用户可访问
hasRole(String)	拥有指定角色可访问（自动加 ROLE_ 前缀）
hasAnyRole(String...)	拥有任一角色可访问
hasAuthority(String)	拥有指定权限可访问
hasAnyAuthority(String...)	拥有任一权限可访问
四、基于方法的权限控制
4.1 常用注解
注解	作用	示例
@PreAuthorize	方法执行前检查权限	@PreAuthorize("hasAuthority('order:read')")
@PostAuthorize	方法执行后检查权限（基于返回值）	@PostAuthorize("returnObject.userId == authentication.principal.id")
@PreFilter	方法执行前过滤集合参数	@PreFilter("filterObject.amount < 1000")
@PostFilter	方法执行后过滤返回值	@PostFilter("filterObject.userId == authentication.principal.id")
4.2 示例代码
java
@Service
public class OrderService {

    // 需要 order:read 权限
    @PreAuthorize("hasAuthority('order:read')")
    public List<Order> getAllOrders() {
        return orderMapper.findAll();
    }

    // ADMIN 角色 或 订单所属用户 可访问
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Order getOrder(Long userId, Long orderId) {
        return orderMapper.findById(orderId);
    }

    // 返回结果中只保留当前用户的订单
    @PostFilter("filterObject.userId == authentication.principal.id")
    public List<Order> getOrdersForCurrentUser() {
        return orderMapper.findAll();
    }

    // 供 SpEL 调用的自定义方法
    public boolean isOwner(Long orderId, Long userId) {
        Order order = orderMapper.findById(orderId);
        return order != null && order.getUserId().equals(userId);
    }
}
五、SpEL 表达式常用写法
表达式	含义
hasRole('ADMIN')	拥有 ROLE_ADMIN 角色（自动加前缀）
hasAnyRole('ADMIN', 'USER')	拥有任一角色
hasAuthority('order:read')	拥有 order:read 权限
authentication.name	当前用户名
authentication.principal.id	当前用户 ID（需自定义 UserDetails）
#参数名	方法参数
#参数名.属性	参数对象的属性
@beanName.method(args)	调用 Spring Bean 的方法
returnObject	返回值（用于 @PostAuthorize、@PostFilter）
filterObject	集合中的元素（用于 @PreFilter、@PostFilter）
六、自定义 UserDetails
java
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    public Long getId() { return id; }
    // 其他 UserDetails 方法实现...
}
java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("用户不存在");
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        user.setRoles(roles);
        return new CustomUserDetails(user);
    }
}
七、密码加密
java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
数据库中存储密码的 BCrypt 密文，例如 123456 的加密结果：

text
$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
八、数据库表结构
sql
-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 用户角色表（支持多角色）
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(50) NOT NULL,
    amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
九、测试接口
接口	权限要求	说明
GET /	所有人	首页
GET /admin	ROLE_ADMIN	仅管理员可访问
GET /user	ROLE_ADMIN 或 ROLE_USER	登录用户可访问
GET /api/orders	order:read 权限	需要权限
GET /api/orders/{id}	ADMIN 或订单所属用户	可访问自己的订单
GET /api/my-orders	登录用户	通过 @PostFilter 返回当前用户订单
十、面试速答
问题	答案
认证和授权的区别？	认证是确认身份，授权是确认权限
hasRole 和 hasAuthority 区别？	hasRole 自动加 ROLE_ 前缀，hasAuthority 不处理
@PreAuthorize 和 @PostAuthorize 区别？	前者执行前检查，后者执行后检查（可基于返回值判断）
@PostFilter 的作用？	方法执行后过滤返回值，只保留符合条件的元素
如何在 SpEL 中获取当前用户 ID？	authentication.principal.id（需自定义 UserDetails）
如何自定义权限判断逻辑？	在 SpEL 中调用 @beanName.method(args)
十一、今日产出
任务	状态
理解授权核心概念	✅
基于 URL 的权限控制	✅
基于方法的权限控制	✅
自定义 UserDetails	✅
@PreAuthorize / @PostFilter 使用	✅