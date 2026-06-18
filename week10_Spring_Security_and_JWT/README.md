# 本周学习总结：Spring Security 深度实战

## 学习时间
2026-06-09 ~ 2026-06-15

---

## 一、学习概览

| 日期 | 学习内容 | 核心知识点 | 状态 |
|------|----------|-----------|------|
| 周一 | Spring Security 认证流程 | UsernamePasswordAuthenticationFilter、ProviderManager、DaoAuthenticationProvider | ✅ |
| 周二 | Spring Security 授权机制 | URL 权限控制、方法级权限、@PreAuthorize、@PostFilter | ✅ |
| 周三 | JWT 集成 | JWT 工具类、认证过滤器、Token 生成/解析/刷新 | ✅ |
| 周四 | OAuth2 集成 GitHub 登录 | 授权码模式、OAuth2 Client、第三方登录 | ✅ |
| 周五 | 自定义认证（手机号登录 + 验证码）| 自定义 Token、Provider、Filter、多方式登录 | ✅ |
| 周六-周日 | LeetCode 刷题 | 滑动窗口、双指针、二分查找、区间合并、BFS | ✅ |

---

## 二、笔记目录
spring-security-week/
├── README.md # 总览（本文件）
├── 01-Spring-Security-认证流程.md
├── 02-Spring-Security-授权机制.md
├── 03-JWT集成.md
├── 04-OAuth2集成GitHub登录.md
├── 05-自定义认证-手机号登录+验证码.md
└── 06-LeetCode刷题.md

text

---

## 三、核心知识点速查

### 3.1 Spring Security 认证流程
请求 → UsernamePasswordAuthenticationFilter → ProviderManager → DaoAuthenticationProvider → SecurityContextHolder

text

| 组件 | 作用 |
|------|------|
| `UsernamePasswordAuthenticationFilter` | 拦截 `/login`，提取用户名密码，创建未认证 Token |
| `ProviderManager` | 遍历 `AuthenticationProvider`，找到支持的进行认证 |
| `DaoAuthenticationProvider` | 调用 `UserDetailsService` 加载用户，比对密码 |
| `SecurityContextHolder` | 使用 `ThreadLocal` 存储认证信息 |

### 3.2 Spring Security 授权机制

**URL 权限控制**：`HttpSecurity.authorizeHttpRequests()`

| 方法 | 作用 |
|------|------|
| `permitAll()` | 所有人可访问 |
| `hasRole("ADMIN")` | 拥有 ADMIN 角色可访问 |
| `hasAuthority("order:read")` | 拥有 order:read 权限可访问 |

**方法级权限控制**：`@PreAuthorize`、`@PostFilter`

| 注解 | 作用 |
|------|------|
| `@PreAuthorize` | 方法执行前检查权限 |
| `@PostFilter` | 方法执行后过滤返回值 |

### 3.3 JWT 集成
登录 → 生成 Token → 前端存储 → 后续请求携带 Token → 过滤器验证 → 存入 SecurityContext

text

**JWT 工具类核心方法**：
- `generateToken()`：生成访问 Token
- `validateToken()`：验证 Token
- `extractUsername()`：提取用户名
- `refreshToken()`：刷新 Token

### 3.4 OAuth2 集成 GitHub 登录
用户点击 GitHub 登录 → 重定向到 GitHub 授权页 → 用户授权 → 返回授权码 → 换取访问令牌 → 获取用户信息 → 创建登录会话

text

**配置要点**：
- 注册 GitHub OAuth App
- 配置 `client-id` 和 `client-secret`
- 使用 `.oauth2Login()` 启用 OAuth2 登录
- 通过 `@AuthenticationPrincipal OAuth2User` 获取用户信息

### 3.5 自定义认证（手机号登录）

**扩展 Spring Security 认证的三大组件**：

| 组件 | 作用 |
|------|------|
| `SmsCodeAuthenticationToken` | 封装手机号和验证码 |
| `SmsCodeAuthenticationProvider` | 校验验证码，加载用户 |
| `SmsCodeAuthenticationFilter` | 拦截 `/login/sms` 请求 |

---

## 四、代码示例

### 4.1 @PreAuthorize 方法权限控制

```java
@PreAuthorize("hasAuthority('order:read')")
public List<Order> getAllOrders() {
    return orderMapper.findAll();
}

@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
public Order getOrder(Long userId, Long orderId) {
    return orderMapper.findById(orderId);
}
4.2 JWT 过滤器核心逻辑
java
String authHeader = request.getHeader("Authorization");
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    String token = authHeader.substring(7);
    if (jwtUtil.validateToken(token)) {
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
4.3 自定义认证 Provider
java
@Component
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) {
        // 1. 校验验证码
        // 2. 加载用户信息
        // 3. 返回已认证 Token
    }
}
五、面试速答卡
问题	答案
Spring Security 认证流程？	Filter → ProviderManager → AuthenticationProvider → UserDetailsService
hasRole 和 hasAuthority 区别？	hasRole 自动加 ROLE_ 前缀，hasAuthority 不处理
JWT 由哪几部分组成？	Header、Payload、Signature
OAuth2 授权码模式步骤？	重定向 → 授权 → 返回授权码 → 换取令牌 → 获取资源
如何扩展手机号登录？	自定义 Token、Provider、Filter 三个组件
@PostFilter 的作用？	方法执行后过滤返回值
六、LeetCode 刷题
序号	题号	题目	难度	核心考点
1	3	无重复字符的最长子串	中等	滑动窗口
2	15	三数之和	中等	双指针 + 排序
3	33	搜索旋转排序数组	中等	二分查找
4	56	合并区间	中等	排序 + 贪心
5	102	二叉树的层序遍历	中等	BFS / 队列
七、完成确认
模块	状态
Spring Security 认证流程	✅
Spring Security 授权机制	✅
JWT 集成	✅
OAuth2 集成 GitHub 登录	✅
自定义认证（手机号登录）	✅
LeetCode 5 题	✅
笔记整理	✅
