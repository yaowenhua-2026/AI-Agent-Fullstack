# OAuth2 入门 - 集成 GitHub 登录

## 学习时间
2026-06-11

---

## 一、项目概述

这是一个基于 Spring Boot 3 + Spring Security 6 的演示项目，展示了如何使用 **OAuth2 协议**实现"使用 GitHub 账号登录"的功能。

### 项目名称
`oauth2-github-demo`

### 技术栈

| 技术 | 说明 |
|------|------|
| Spring Boot 3.5.14 | 基础框架 |
| Spring Security 6 | 安全框架 |
| Spring OAuth2 Client | OAuth2 客户端支持 |
| Thymeleaf | 模板引擎（渲染 HTML） |
| GitHub OAuth API | 第三方认证服务 |

---

## 二、OAuth2 原理回顾

### 2.1 什么是 OAuth2？

OAuth2 是一个**授权框架**，允许第三方应用在用户授权的前提下，访问用户存储在另一服务上的资源，而无需获取用户的密码。

### 2.2 授权码模式流程

| 步骤 | 说明 |
|------|------|
| 1 | 用户访问客户端应用，点击"GitHub 登录" |
| 2 | 客户端重定向到 GitHub 的授权页面 |
| 3 | 用户登录 GitHub 并授权 |
| 4 | GitHub 重定向回客户端，并附带一个**授权码** |
| 5 | 客户端使用授权码向 GitHub 换取**访问令牌** |
| 6 | 客户端使用访问令牌调用 GitHub API 获取用户信息 |
| 7 | 客户端根据用户信息创建登录会话 |

### 2.3 核心角色

| 角色 | 本例中对应 |
|------|------------|
| 资源所有者 | GitHub 用户 |
| 客户端 | 本项目（`oauth2-github-demo`）|
| 授权服务器 | GitHub OAuth 服务 |
| 资源服务器 | GitHub API（用户信息）|

---

## 三、GitHub OAuth 应用注册

### 3.1 注册入口

GitHub Settings → Developer settings → OAuth Apps → New OAuth App

### 3.2 注册参数

| 参数 | 填写值 |
|------|--------|
| Homepage URL | `http://localhost:8080` |
| Authorization callback URL | `http://localhost:8080/login/oauth2/code/github` |

### 3.3 获取凭证

注册成功后，获得：
- **Client ID**：`Ov23lic8oGaqeu0hB3cl`
- **Client Secret**：`dedd66edd9c45dbd682f424f1ee1431ff4902b6f`

> ⚠️ 实际生产环境请勿暴露 Secret，应使用环境变量或配置中心。

---

## 四、Spring Boot 集成配置

### 4.1 添加依赖（pom.xml）

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
4.2 配置文件（application.yaml）
yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: Ov23lic8oGaqeu0hB3cl
            client-secret: dedd66edd9c45dbd682f424f1ee1431ff4902b6f
            scope:
              - read:user
              - user:email
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          github:
            authorization-uri: https://github.com/login/oauth/authorize
            token-uri: https://github.com/login/oauth/access_token
            user-info-uri: https://api.github.com/user
            user-name-attribute: login
参数说明
参数	含义
registration	定义 OAuth2 客户端注册信息（如 client-id、secret）
provider	定义授权服务器元数据（URI 端点、用户名属性）
scope	请求的权限范围（读取用户信息和邮箱）
redirect-uri	GitHub 回调地址，{baseUrl} 自动替换为 http://localhost:8080
五、安全配置（SecurityConfig.java）
java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/dashboard", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }
}
配置解读
配置	作用
.requestMatchers("/", "/index").permitAll()	首页无需登录即可访问
.anyRequest().authenticated()	其他所有请求都需要认证
.oauth2Login()	启用 OAuth2 登录，自动生成 /oauth2/authorization/github 入口
.defaultSuccessUrl("/dashboard", true)	登录成功后强制跳转到 /dashboard
.logoutSuccessUrl("/")	退出后返回首页
六、控制器和模板
6.1 HomeController.java
java
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User != null) {
            Map<String, Object> attrs = oAuth2User.getAttributes();
            model.addAttribute("username", attrs.get("login"));
            model.addAttribute("name", attrs.get("name"));
            model.addAttribute("avatarUrl", attrs.get("avatar_url"));
            model.addAttribute("email", attrs.get("email"));
        }
        return "dashboard";
    }
}
@Controller 返回视图名称，由 Thymeleaf 渲染 HTML。

@AuthenticationPrincipal OAuth2User 自动注入当前登录用户的 GitHub 信息。

6.2 模板页面
index.html（首页）

html
<a href="/oauth2/authorization/github" class="btn">使用 GitHub 登录</a>
dashboard.html（个人中心）

html
<img th:src="${avatarUrl}" class="avatar"/>
<p>用户名：<span th:text="${username}"></span></p>
<p>姓名：<span th:text="${name}"></span></p>
<p>邮箱：<span th:text="${email}"></span></p>
<a href="/logout">退出登录</a>
七、完整登录流程
text
1. 用户访问 http://localhost:8080
   ↓
2. 点击"使用 GitHub 登录"按钮
   ↓
3. 浏览器跳转到 GitHub 授权页
   ↓
4. 用户登录 GitHub 并授权
   ↓
5. GitHub 重定向到 http://localhost:8080/login/oauth2/code/github?code=xxx
   ↓
6. Spring Security 自动处理：
   - 用 code 换 access_token
   - 用 access_token 调 https://api.github.com/user 获取用户信息
   - 创建 OAuth2User 对象并存入 SecurityContext
   ↓
7. 重定向到 /dashboard
   ↓
8. dashboard 页面从 OAuth2User 中提取用户信息并展示
八、常见问题及解决方案
问题	原因	解决
redirect_uri_mismatch	回调 URL 与 GitHub 注册的不一致	确保 redirect-uri 配置与 GitHub App 中填写的完全一致
用户邮箱为 null	未请求 user:email 权限	在 scope 中添加 user:email
登录后仍无法访问 /dashboard	未配置 defaultSuccessUrl	添加 .defaultSuccessUrl("/dashboard", true)
退出后仍能访问	未配置 logout	添加 .logout(logout -> logout.logoutSuccessUrl("/"))
九、项目结构
text
oauth2-github-demo/
├── src/main/java/com/yll/oauth2githubdemo/
│   ├── Oauth2GithubDemoApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   └── controller/
│       └── HomeController.java
└── src/main/resources/
    ├── application.yaml
    └── templates/
        ├── index.html
        └── dashboard.html
十、面试速答
问题	答案
OAuth2 授权码模式的步骤	客户端重定向 → 用户授权 → 返回授权码 → 换取令牌 → 获取资源
redirect-uri 为什么必须和注册一致？	安全考虑，防止授权码被恶意截获
scope 的作用	限制客户端访问的资源范围
@AuthenticationPrincipal OAuth2User 如何获取信息？	Spring Security 自动将认证用户注入
如何自定义用户信息存储？	实现 OAuth2UserService 接口