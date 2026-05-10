```markdown
# Redis 持久化：RDB 与 AOF - 学习总结

## 学习时间
2026-05-06

---

## 一、为什么需要持久化？

Redis 是内存数据库，数据存在内存中。**断电、重启后数据会丢失。**

| 问题 | 解决方案 |
|------|----------|
| 数据丢失 | 持久化到磁盘 |
| 重启恢复 | 从磁盘加载数据 |

---

## 二、RDB（Redis Database）

### 2.1 原理
**RDB = 内存快照**，把某一时刻的数据全量写入磁盘文件 `dump.rdb`。

### 2.2 触发方式

| 方式 | 命令 | 说明 |
|------|------|------|
| 手动触发 | `BGSAVE` | fork 子进程，不阻塞（推荐）|
| 手动触发 | `SAVE` | 阻塞主进程（不推荐）|
| 自动触发 | `save 900 1` | 900秒内至少1次修改 |

### 2.3 优缺点

| 优点 | 缺点 |
|------|------|
| 文件小（压缩）| 可能丢数据（最后一次快照后）|
| 恢复快 | 大内存时 fork 慢 |
| 性能好（fork 子进程）| |

---

## 三、AOF（Append Only File）

### 3.1 原理
**AOF = 追加写**，把每条写命令追加到文件。

### 3.2 同步策略

| 策略 | 命令 | 说明 | 可能丢数据 |
|------|------|------|-----------|
| **always** | `appendfsync always` | 每次写都同步 | 0 条 |
| **everysec** | `appendfsync everysec` | 每秒同步一次 | 最多1秒 |
| **no** | `appendfsync no` | 操作系统决定 | 不确定 |

**推荐**：`appendfsync everysec`

### 3.3 AOF 重写

```bash
# 手动触发重写
BGREWRITEAOF

# 自动重写配置
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
3.4 优缺点
优点	缺点
数据完整（最多丢1秒）	文件大
可读性强（文本命令）	恢复慢
可手动修复	性能略低
四、Redis 7.4.2 持久化文件结构
text
Redis 目录/
├── dump.rdb                    # RDB 快照文件
└── appendonlydir/              # AOF 目录
    ├── appendonly.aof.1.base.rdb   # 基础 RDB 快照
    ├── appendonly.aof.1.incr.aof   # 增量 AOF 命令
    └── appendonly.aof.manifest     # 清单文件
文件	作用
dump.rdb	RDB 快照
base.rdb	混合持久化的 RDB 部分
incr.aof	增量写命令
manifest	文件版本管理
五、混合持久化（Redis 4.0+）
5.1 配置
conf
# 开启混合持久化
aof-use-rdb-preamble yes
5.2 文件内容
文件	格式	内容
base.rdb	二进制	内存快照（乱码）
incr.aof	文本	写命令（可读）
六、RDB vs AOF 对比
对比项	RDB	AOF
文件大小	小	大
恢复速度	快	慢
数据完整性	可能丢数据	最多丢1秒
性能影响	小	中等
可读性	二进制	文本
适用场景	备份、快速恢复	数据安全要求高
七、生产环境配置方案
conf
# RDB 配置
save 900 1
save 300 10
save 60 10000

# AOF 配置
appendonly yes
appendfsync everysec
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb

# 混合持久化
aof-use-rdb-preamble yes
八、实验验证
bash
# RDB 实验
BGSAVE
LASTSAVE

# AOF 实验
CONFIG SET appendonly yes
CONFIG SET appendfsync everysec
SET aof-test "hello"
BGREWRITEAOF

# 混合持久化
CONFIG SET aof-use-rdb-preamble yes
BGREWRITEAOF

# 数据恢复验证
FLUSHALL
redis-server --appendonly yes
KEYS *
GET aof-test
九、面试速答
问题	答案
RDB 是什么？	内存快照，全量备份
AOF 是什么？	追加写命令，增量备份
两者区别？	RDB 恢复快但可能丢数据，AOF 数据完整但恢复慢
生产环境推荐？	RDB + AOF 混合持久化
AOF 同步策略？	always（最安全）、everysec（推荐）、no
Redis 7 的 AOF 格式？	base.rdb + incr.aof 分离
十、今日产出
任务	状态
理解 RDB 原理	✅
手动触发 BGSAVE	✅
理解 AOF 原理	✅
开启 AOF 配置	✅
理解 AOF 重写	✅
理解混合持久化	✅
数据恢复验证	✅