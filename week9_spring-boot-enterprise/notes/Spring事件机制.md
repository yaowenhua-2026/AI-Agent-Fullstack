### 5. Spring事件机制.md

```markdown
# Spring 事件机制

## 学习时间
2026-05-24

---

## 一、核心组件

| 组件 | 作用 |
|------|------|
| **事件** | 封装需要传递的信息 |
| **事件发布器** | 发布事件 |
| **事件监听器** | 处理事件 |

---

## 二、代码结构
com.yll.myspringstarter/
├── event/
│ └── UserRegisterEvent.java # 事件类
├── listener/
│ ├── UserRegisterListener.java # 监听器（@EventListener）
│ ├── UserRegisterListener2.java # 监听器（ApplicationListener接口）
│ └── UserRegisterListener3.java # 监听器（条件过滤）
└── service/
└── UserService.java # 事件发布者

text

---

## 三、核心代码

### 3.1 事件类

```java
public class UserRegisterEvent extends ApplicationEvent {
    private final String username;
    private final String email;

    public UserRegisterEvent(Object source, String username, String email) {
        super(source);
        this.username = username;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
3.2 事件发布者
java
@Service
public class UserService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void register(String username, String email) {
        // 业务逻辑
        System.out.println("保存用户 " + username + " 到数据库");
        
        // 发布事件
        UserRegisterEvent event = new UserRegisterEvent(this, username, email);
        eventPublisher.publishEvent(event);
    }
}
3.3 监听器实现方式
方式一：@EventListener 注解（推荐）

java
@Component
public class UserRegisterListener {
    
    @EventListener
    public void handleSendEmail(UserRegisterEvent event) {
        System.out.println("发送欢迎邮件给: " + event.getEmail());
    }

    @EventListener
    public void handleSendCoupon(UserRegisterEvent event) {
        System.out.println("赠送优惠券给: " + event.getUsername());
    }

    @EventListener
    public void handleLog(UserRegisterEvent event) {
        System.out.println("记录日志: 用户 " + event.getUsername() + " 注册成功");
    }
}
方式二：实现 ApplicationListener 接口

java
@Component
public class UserRegisterListener2 implements ApplicationListener<UserRegisterEvent> {
    @Override
    public void onApplicationEvent(UserRegisterEvent event) {
        System.out.println("【接口方式】用户注册: " + event.getUsername());
    }
}
方式三：条件过滤

java
public class UserRegisterListener3 {
    
    @EventListener(condition = "#event.username.startsWith('admin')")
    public void handleAdminRegister(UserRegisterEvent event) {
        System.out.println("【管理员注册特殊处理】" + event.getUsername());
    }
}
四、运行结果
text
=== 执行注册逻辑：保存用户 张三 到数据库 ===
【监听器1】发送欢迎邮件给: zhangsan@example.com
【监听器2】赠送优惠券给: 张三
【监听器3】记录日志: 用户 张三 注册成功
【监听器4（接口方式）】用户注册: 张三
=== 注册完成，事件已发布 ===
五、三种监听方式对比
方式	优点	缺点	使用场景
@EventListener	简单灵活，可多方法	需要 Spring 4.2+	最常用
ApplicationListener	类型安全，泛型明确	每个事件需单独实现类	需要明确类型时
@EventListener + condition	支持条件过滤	表达式语法	需要条件判断时
六、高级特性
6.1 条件过滤
java
@EventListener(condition = "#event.username.startsWith('admin')")
6.2 异步监听（需加 @EnableAsync）
java
@Async
@EventListener
public void handleAsync(UserRegisterEvent event) {
    // 异步处理
}
6.3 事务事件（需加 @TransactionalEventListener）
java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleAfterCommit(UserRegisterEvent event) {
    // 事务提交后执行
}
七、执行流程
text
UserService.register()
    ↓
保存用户到数据库
    ↓
eventPublisher.publishEvent()
    ↓
Spring 事件广播器
    ↓
遍历所有监听器
    ↓
UserRegisterListener.handleSendEmail()
UserRegisterListener.handleSendCoupon()
UserRegisterListener.handleLog()
UserRegisterListener2.onApplicationEvent()
UserRegisterListener3.handleAdminRegister()（条件满足时）
八、事件机制 vs 直接调用
对比	事件机制	直接调用
耦合度	低	高
扩展性	新增监听器无需改代码	需要修改调用代码
调试	较难跟踪	容易
适用场景	多步骤、可扩展业务	简单、固定流程
九、面试速答
问题	答案
Spring 事件机制是什么？	观察者模式，用于解耦业务逻辑
核心组件有哪些？	事件、发布器、监听器
如何发布事件？	注入 ApplicationEventPublisher，调用 publishEvent
如何监听事件？	@EventListener 或 ApplicationListener
如何条件过滤？	@EventListener(condition = "...#event.xxx")
如何异步处理？	@Async + @EventListener + @EnableAsync
text

---

### 6. LeetCode图算法.md

```markdown
# LeetCode 图算法

## 学习时间
2026-05-25 ~ 2026-05-26

---

## 一、题目清单

| 序号 | 题号 | 题目 | 难度 | 核心考点 |
|------|------|------|------|----------|
| 1 | 200 | 岛屿数量 | 中等 | DFS/BFS 遍历图 |
| 2 | 994 | 腐烂的橘子 | 中等 | 多源 BFS |

---

## 二、岛屿数量（LeetCode 200）

### 题目描述

给定一个由 `'1'`（陆地）和 `'0'`（水）组成的二维网格，计算岛屿的数量。

### 代码实现

```java
class Solution {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        
        int m = grid.length;
        int n = grid[0].length;
        int count = 0;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    dfs(grid, i, j);
                    count++;
                }
            }
        }
        return count;
    }
    
    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length 
            || grid[i][j] != '1') {
            return;
        }
        
        grid[i][j] = '0';  // 标记已访问
        dfs(grid, i + 1, j);
        dfs(grid, i - 1, j);
        dfs(grid, i, j + 1);
        dfs(grid, i, j - 1);
    }
}
三、腐烂的橘子（LeetCode 994）
题目描述
每分钟，腐烂的橘子会将其上下左右四个方向的新鲜橘子腐烂。返回直到单元格中没有新鲜橘子为止所必须经过的最小分钟数。

代码实现
java
class Solution {
    public int orangesRotting(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        
        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;
        
        // 1. 统计新鲜橘子数量，腐烂橘子入队
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                } else if (grid[i][j] == 1) {
                    fresh++;
                }
            }
        }
        
        if (fresh == 0) return 0;
        
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int minutes = 0;
        
        // 2. BFS 扩散
        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] pos = queue.poll();
                for (int[] dir : dirs) {
                    int x = pos[0] + dir[0];
                    int y = pos[1] + dir[1];
                    if (x < 0 || x >= m || y < 0 || y >= n) continue;
                    if (grid[x][y] != 1) continue;
                    
                    grid[x][y] = 2;
                    queue.offer(new int[]{x, y});
                    fresh--;
                }
            }
            minutes++;
        }
        
        return fresh == 0 ? minutes : -1;
    }
}
四、DFS 模板（网格图）
java
void dfs(int[][] grid, int i, int j) {
    if (越界 || 不是目标) return;
    标记已访问;
    dfs(grid, i+1, j);
    dfs(grid, i-1, j);
    dfs(grid, i, j+1);
    dfs(grid, i, j-1);
}
五、BFS 模板（最短路径）
java
Queue<int[]> queue = new LinkedList<>();
queue.offer(start);
visited[start] = true;
int step = 0;

while (!queue.isEmpty()) {
    int size = queue.size();
    for (int i = 0; i < size; i++) {
        int[] curr = queue.poll();
        for (int[] next : 邻接) {
            if (!visited[next]) {
                visited[next] = true;
                queue.offer(next);
            }
        }
    }
    step++;
}
六、完成确认
序号	题目	状态
1	200. 岛屿数量	✅
2	994. 腐烂的橘子	✅