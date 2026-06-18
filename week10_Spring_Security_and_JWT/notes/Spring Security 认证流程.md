文件1：Spring Security 认证流程.md
markdown
# Spring Security 认证流程

## 学习时间
2026-05-25

---

## 一、认证流程图
┌─────────────────────────────────────────────────────────────────────────────┐
│ Spring Security 认证流程 │
├─────────────────────────────────────────────────────────────────────────────┤
│ │
│ 前端请求 (POST /login) │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────────┐ │
│ │ ① UsernamePasswordAuthenticationFilter (门卫) │ │
│ │ - 提取 username / password │ │
│ │ - 创建 未认证 Token │ │
│ │ principal = "张三", credentials = "123", authenticated = false │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────────┐ │
│ │ ② ProviderManager (调度中心) │ │
│ │ - 遍历所有 AuthenticationProvider │ │
│ │ - 调用 supports() 找到支持 UsernamePasswordAuthenticationToken 的 │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────────┐ │
│ │ ③ DaoAuthenticationProvider (审核员) │ │
│ │ - retrieveUser() → 调用 UserDetailsService 查数据库 │ │
│ │ - additionalAuthenticationChecks() → 用 PasswordEncoder 比对密码 │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────────┐ │
│ │ ④ 认证成功 ✅ │ │
│ │ - 创建 已认证 Token │ │
│ │ principal = UserDetails对象, credentials = null, authorities=[..]│ │
│ │ authenticated = true │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────────┐ │
│ │ ⑤ SecurityContextHolder (保险箱) │ │
│ │ - SecurityContextHolder.getContext().setAuthentication(authToken) │ │
│ │ - 使用 ThreadLocal 存储，当前线程可随时获取 │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────────┐ │
│ │ ⑥ 返回给前端（二选一） │ │
│ │ │ │
│ │ 【Session 模式】 【JWT 模式】 │ │
│ │ - 自动创建 Session - 生成 JWT 字符串 │ │
│ │ - SessionId 放入 Cookie - 返回 JSON: {token: "xxx"} │ │
│ │ - 前端不需要手动处理 - 前端存储到 localStorage │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ │
└─────────────────────────────────────────────────────────────────────────────┘

text

---

## 二、核心组件

| 组件 | 作用 |
|------|------|
| **UsernamePasswordAuthenticationFilter** | 拦截 `/login` 请求，提取用户名密码，创建未认证 Token |
| **ProviderManager** | 遍历 `AuthenticationProvider`，找到支持的进行认证 |
| **DaoAuthenticationProvider** | 调用 `UserDetailsService` 加载用户，用 `PasswordEncoder` 比对密码 |
| **SecurityContextHolder** | 使用 `ThreadLocal` 存储认证信息，当前线程可随时获取 |

---

## 三、认证前后 Token 变化

| 状态 | principal | credentials | authenticated |
|------|-----------|-------------|---------------|
| **认证前** | 用户名（String）| 密码（String）| false |
| **认证后** | UserDetails 对象 | null | true |

---

## 四、核心代码分析

### 4.1 UsernamePasswordAuthenticationFilter

```java
public Authentication attemptAuthentication(HttpServletRequest request,
                                            HttpServletResponse response) {
    String username = obtainUsername(request);
    String password = obtainPassword(request);
    
    UsernamePasswordAuthenticationToken authRequest = 
        new UsernamePasswordAuthenticationToken(username, password);
    
    setDetails(request, authRequest);
    
    return this.getAuthenticationManager().authenticate(authRequest);
}
4.2 DaoAuthenticationProvider
java
// 加载用户
protected final UserDetails retrieveUser(String username, 
        UsernamePasswordAuthenticationToken authentication) {
    return this.getUserDetailsService().loadUserByUsername(username);
}

// 密码比对
protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication) {
    String presentedPassword = authentication.getCredentials().toString();
    if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
        throw new BadCredentialsException("Bad credentials");
    }
}
4.3 认证成功后存储
java
// 认证成功后
SecurityContextHolder.getContext().setAuthentication(authResult);

// 后续获取
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
五、自定义配置
java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/user/login")           // 登录页面地址
                .loginProcessingUrl("/user/login")  // 提交登录请求的地址
                .usernameParameter("userName")      // 自定义用户名参数名
                .passwordParameter("passWord")      // 自定义密码参数名
                .successHandler(customSuccessHandler())
                .failureHandler(customFailureHandler())
                .permitAll()
            );
        return http.build();
    }
}
六、两种认证模式对比
模式	Session 模式	JWT 模式
认证后返回	SessionId（自动写 Cookie）	JWT 字符串（JSON）
存储位置	服务端 Session	前端 localStorage
后续请求携带	Cookie 自动携带	Header: Authorization: Bearer <jwt>
状态	有状态	无状态
七、面试速答
问题	答案
UsernamePasswordAuthenticationFilter 的作用？	拦截登录请求，提取用户名密码，创建未认证 Token
ProviderManager 如何找 Provider？	遍历，调用 supports() 判断
密码比对的步骤？	additionalAuthenticationChecks() 中调用 PasswordEncoder.matches()
密码不匹配抛什么异常？	BadCredentialsException
SecurityContextHolder 存储机制？	ThreadLocal，同一请求线程可随时获取
如何自定义登录路径和参数？	loginProcessingUrl()、usernameParameter()、passwordParameter()
八、今日产出
任务	状态
理解 UsernamePasswordAuthenticationFilter	✅
理解 ProviderManager 调度机制	✅
理解 DaoAuthenticationProvider 认证逻辑	✅
理解 SecurityContextHolder 存储机制	✅
理解 Session vs JWT 模式	✅
掌握自定义配置	✅
绘制认证流程图	✅