# MySQL 性能优化 - 一周学习总结

## 学习时间
2026-04-28 ~ 2026-05-02

## 学习内容

本周系统学习了 MySQL 性能优化的核心知识，包括：
- 聚合查询优化（汇总表、临时表）
- 关联查询优化（Join 算法、索引优化）
- 分页查询优化（游标分页、软删除）
- 批量操作优化（批量插入、LOAD DATA）
- 数据库设计规范（三范式、主表+明细表）

---

## 一、聚合查询优化

### 问题
大表做 `SUM`、`COUNT`、`GROUP BY` 需要全表扫描，非常慢。

### 解决方案

| 方案 | 原理 | 适用场景 |
|------|------|---------|
| **汇总表** | 提前算好结果，存起来 | 固定维度的统计报表 |
| **临时表** | 分批处理，减少锁 | 一次性复杂查询 |
| **覆盖索引** | 索引包含查询字段 | 简单聚合 |

### 性能对比

| 查询方式 | 耗时 | 扫描行数 |
|---------|------|---------|
| 直接聚合 | 68.8ms | 全表（数千行）|
| 汇总表（7天）| <1ms | 7 行 |

**提升约 70 倍！**

### 汇总表实现

```sql
-- 创建汇总表
CREATE TABLE order_stats (
    stat_date DATE PRIMARY KEY,
    order_count INT,
    total_amount DECIMAL(10,2),
    update_time DATETIME
);

-- 定时更新
INSERT INTO order_stats (stat_date, order_count, total_amount, update_time)
SELECT DATE(create_time), COUNT(*), SUM(amount), NOW()
FROM `order`
WHERE DATE(create_time) = CURDATE() - INTERVAL 1 DAY
GROUP BY DATE(create_time)
ON DUPLICATE KEY UPDATE
    order_count = VALUES(order_count),
    total_amount = VALUES(total_amount),
    update_time = VALUES(update_time);
二、关联查询优化
三种 Join 算法
算法	原理	复杂度	MySQL 版本
NLJ（嵌套循环）	驱动表每行去被驱动表匹配	O(N×M)	所有版本
BNL（块嵌套循环）	驱动表分批，放入内存匹配	O(N×M/R)	5.6+
Hash Join	建立哈希表匹配	O(N+M)	8.0.18+
优化效果
状态	被驱动表 type	被驱动表 rows	效果
不加索引	ALL	100078	全表扫描 ❌
加索引	ref	10	索引查找 ✅
优化清单
优化策略	说明
✅ 被驱动表加索引	最关键！让 NLJ 能用索引
✅ 小表驱动大表	减少驱动表遍历次数
✅ 只查需要的列	减少数据传输
✅ 用 EXPLAIN 分析	找出慢的原因
三、分页查询优化
两种分页方式对比
对比项	limit 分页	游标分页
跳页	✅ 能	❌ 不能
下一页	✅ 能	✅ 能
深度分页性能	❌ 越往后越慢	✅ 快
原理	扫描所有数据，跳过偏移量	WHERE id > last_id
Explain 验证
分页方式	type	rows
limit 分页	index	625（实际 50 万行）
游标分页	range	1 ✅
优化方案：软删除 + 游标分页
sql
-- 软删除（不真删，只标记）
UPDATE user SET is_deleted = 1 WHERE id = 500;

-- 游标分页（id 连续，可以跳页）
SELECT * FROM user WHERE is_deleted = 0 AND id > ? LIMIT 10;
适用场景
场景	推荐方案
后台管理（数据量小）	limit 分页
App 信息流	游标分页
订单/用户表	软删除 + 游标
日志数据	限制最大页码
四、批量操作优化
性能对比
插入方式	耗时	网络往返	事务次数
逐条插入（1万条）	18.04 秒	10,000 次	10,000 次
批量插入（1万条）	0.28 秒	1 次	1 次
批量插入比逐条插入快约 65 倍！

批量插入示例
sql
INSERT INTO user (name, age) VALUES 
('user1', 1),
('user2', 2),
...
('user10000', 10000);
优化清单
优化策略	说明	优先级
✅ 批量插入	一条 SQL 插多条数据	高
✅ 控制批量大小	500-2000 条/批	高
✅ 手动控制事务	每批提交一次	中
✅ 使用批量 API	JdbcTemplate.batchUpdate	中
🟡 LOAD DATA	百万级数据导入	低
五、数据库设计规范
三范式
范式	要求	说明
第一范式（1NF）	列不可分	每个字段都是原子值
第二范式（2NF）	完全依赖主键	消除部分依赖
第三范式（3NF）	没有传递依赖	消除间接依赖
订单表设计
订单主表 orders

sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    pay_amount DECIMAL(10,2) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL,
    UNIQUE INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id)
);
订单明细表 order_items

sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    INDEX idx_order_id (order_id)
);
设计规范清单
规范	说明
主键	BIGINT + AUTO_INCREMENT
金额	DECIMAL(10,2)，不用 DOUBLE
状态	TINYINT + COMMENT 注释
时间	DATETIME
手机号	VARCHAR(20)，不用 INT
命名	小写 + 下划线，表名用复数
六、面试速答
问题	答案
大表 count 慢怎么办？	用汇总表提前统计
join 慢怎么办？	被驱动表加索引
limit 分页为什么慢？	需要扫描前面所有数据
批量插入为什么快？	减少网络往返、SQL 解析、事务开销
订单表怎么设计？	主表存订单信息，明细表存商品信息
三范式是什么？	1NF 列不可分，2NF 完全依赖主键，3NF 没有传递依赖
七、文档目录
文件	说明
聚合查询优化.docx	汇总表、临时表、覆盖索引
关联查询优化.docx	Join 算法、索引优化、优化清单
分页查询优化总结.docx	limit 分页、游标分页、软删除
批量操作优化总结.docx	逐条插入 vs 批量插入、LOAD DATA
数据库设计规范.docx	三范式、订单表设计、设计规范