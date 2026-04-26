# Explain 详解 + 慢 SQL 优化案例

## 一、Explain 是什么？

**Explain = MySQL 的执行计划分析工具**

```sql
EXPLAIN SELECT * FROM user WHERE name = '张三';
告诉你：MySQL 怎么执行这条 SQL，用没用索引，扫描了多少行。

二、Explain 关键字段
字段	含义	好/坏
type	访问类型	ref > range > ALL
key	实际使用的索引	有值 ✅，NULL ❌
rows	预估扫描行数	越少越好
Extra	额外信息	Using index 好
三、type 类型排名（好 → 差）
type	含义	例子
const	主键/唯一索引等值查询	WHERE id = 1
ref	普通索引等值查询	WHERE name = '张三'
range	范围查询	WHERE age > 20
index	全索引扫描	比全表快一点
ALL	全表扫描 ❌	没有索引
四、Extra 关键信息
Extra	含义	好/坏
Using index	覆盖索引，不回表	✅ 好
Using where	需要回表过滤	🟡 一般
Using filesort	需要额外排序	❌ 差
Using temporary	需要临时表	❌ 差
五、慢 SQL 优化案例集
案例1：无索引 → 加索引
状态	type	key	rows
优化前	ALL	NULL	100000
优化后	ref	idx_name	1
优化方案：CREATE INDEX idx_name ON user (name);

案例2：函数导致索引失效
状态	type	key
优化前	ALL	NULL
问题：WHERE LEFT(name, 2) = '张三'

优化：WHERE name LIKE '张三%'

案例3：隐式类型转换
状态	type	key
优化前	ALL	NULL
问题：WHERE phone = 13800138000（phone 是 VARCHAR）

优化：WHERE phone = '13800138000'

案例4：最左前缀失效
状态	type	key
优化前	ALL	NULL
问题：WHERE age = 20（联合索引是 (name, age)）

优化：WHERE name = '张三' AND age = 20

案例5：Using filesort
状态	type	Extra
优化前	ref	Using filesort
优化后	ref	(空)
优化方案：CREATE INDEX idx_name_age ON user (name, age);

六、索引失效场景总结
场景	错误写法	正确写法
函数	LEFT(name,2) = '张三'	name LIKE '张三%'
隐式转换	phone = 13800138000	phone = '13800138000'
最左前缀	WHERE age = 20	WHERE name = '张三' AND age = 20
七、慢 SQL 优化步骤
text
1. 开启慢查询日志
2. 找到慢 SQL
3. 用 Explain 分析
4. 根据结果优化
   - type 是 ALL → 加索引
   - key 是 NULL → 加索引
   - rows 很大 → 优化条件
   - Extra 有 filesort/temporary → 优化排序/分组
5. 验证优化效果
八、今日产出
任务	状态
理解 Explain 字段	✅
分析慢 SQL 案例	✅
记录优化方案	✅