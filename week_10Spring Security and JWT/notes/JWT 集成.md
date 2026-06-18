JWT 集成.md

```markdown
# JWT 集成

## 学习时间
2026-06-11

---

## 一、项目结构
spring-security-jwt-demo/
├── config/
│ └── SecurityConfig.java # 安全配置（禁用Session、JWT过滤器）
├── controller/
│ └── AuthController.java # 登录接口
├── dto/
│ ├── LoginRequest.java # 登录请求
│ └── LoginResponse.java # Token响应
├── entity/
│ ├── User.java # 用户实体
│ └── CustomUserDetails.java # Spring Security 用户详情
├── filter/
│ └── JwtAuthenticationFilter.java # JWT认证过滤器
├── mapper/
│ └── UserMapper.java # MyBatis 数据访问
├── service/
│ └── CustomUserDetailsService.java # 用户详情服务
├── util/
│ └── JwtUtil.java # JWT工具类
└── resources/
├── application.yaml # 配置文件
├── schema.sql # 表结构
└── data.sql # 测试数据

text

---

## 二、JWT 工具类核心功能

| 方法 | 功能 |
|------|------|
| `generateToken()` | 生成普通访问 Token |
| `generateRefreshToken()` | 生成刷新 Token |
| `extractUsername()` | 从 Token 中提取用户名 |
| `extractUserId()` | 从 Token 中提取用户 ID |
| `extractRoles()` | 从 Token 中提取角色列表 |
| `validateToken()` | 验证 Token 是否有效（签名+过期） |
| `isTokenExpired()` | 判断 Token 是否过期 |
| `refreshToken()` | 刷新 Token |

---

## 三、认证流程
用户登录（POST /auth/login）
↓

AuthenticationManager 验证用户名密码
↓

验证成功 → 生成 Access Token + Refresh Token
↓

返回 Token 给前端
↓

前端后续请求携带 Token（Header: Authorization: Bearer <token>）
↓

JwtAuthenticationFilter 拦截请求，解析并验证 Token
↓

验证通过 → 将用户信息存入 SecurityContextHolder
↓

请求放行，进入业务接口

text

---

## 四、关键代码说明

### 4.1 SecurityConfig 配置要点

```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 无状态
)
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
STATELESS：不创建 Session，每次请求都依赖 JWT 认证

addFilterBefore：在 Spring Security 默认认证过滤器之前插入 JWT 过滤器

4.2 JwtAuthenticationFilter 核心逻辑
java
String authHeader = request.getHeader("Authorization");
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    String token = authHeader.substring(7);
    if (jwtUtil.validateToken(token)) {
        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        // 创建认证对象并存入 SecurityContextHolder
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
filterChain.doFilter(request, response);
4.3 JWT 生成
java
public String generateToken(Long userId, String username, List<String> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("roles", roles);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
}
五、测试接口
5.1 登录请求
bash
POST /auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "123456"
}
5.2 登录响应
json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
5.3 携带 Token 访问受保护接口
bash
GET /api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
六、配置文件
yaml
jwt:
  secret: "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
  expiration: 3600000          # 1 小时
  refresh-expiration: 604800000 # 7 天
七、Token 刷新机制
text
Access Token 过期（7天内的过期）
   ↓
使用 Refresh Token 调用 /auth/refresh
   ↓
验证 Refresh Token 有效
   ↓
生成新的 Access Token
   ↓
返回新 Token 给前端
八、JWT vs Session 对比
对比项	Session	JWT
存储位置	服务端	客户端
状态	有状态	无状态
分布式支持	需共享 Session	天然支持
注销	删除 Session	需黑名单或短过期
性能	查 Session 有开销	解析 Token 无状态
适用场景	单体应用	微服务、前后端分离
九、常见异常处理
异常	含义
ExpiredJwtException	Token 已过期
MalformedJwtException	Token 格式错误
SignatureException	签名验证失败
UnsupportedJwtException	不支持的 Token
IllegalArgumentException	Token 参数异常
十、今日产出
任务	状态
JWT 工具类（生成、解析、刷新）	✅
JWT 认证过滤器	✅
登录接口集成 JWT	✅
无状态 Security 配置	✅
配置文件与数据初始化	✅