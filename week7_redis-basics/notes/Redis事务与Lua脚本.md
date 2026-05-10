
```markdown
# Redis 事务与 Lua 脚本 - 学习总结

## 学习时间
2026-05-08

---

## 一、项目架构

| 组件 | 技术 | 作用 |
|------|------|------|
| 库存扣减 | Redis + Lua | 原子性扣减 |
| 订单记录 | MySQL | 持久化订单 |
| 压测工具 | JMeter | 并发测试 |

---

## 二、Lua 脚本核心代码

```lua
local stock = redis.call('get', 'stock:' .. KEYS[1])
if stock and tonumber(stock) > 0 then
    redis.call('decr', 'stock:' .. KEYS[1])
    return 1
else
    return 0
end
脚本说明
步骤	说明
redis.call('get', key)	查询库存
tonumber(stock) > 0	判断库存 > 0
redis.call('decr', key)	库存减 1
return 1/0	返回成功/失败
三、Java 代码调用
java
@Service
public class SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LUA_SCRIPT =
        "local stock = redis.call('get', 'stock:' .. KEYS[1]) " +
        "if stock and tonumber(stock) > 0 then " +
        "    redis.call('decr', 'stock:' .. KEYS[1]) " +
        "    return 1 " +
        "else " +
        "    return 0 " +
        "end";

    private final DefaultRedisScript<Long> redisScript;

    public SeckillService() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);
    }

    public boolean seckill(String productCode, Long userId, String orderNo) {
        Long result = redisTemplate.execute(
            redisScript,
            Collections.singletonList(productCode),
            userId.toString(), orderNo
        );

        if (result != null && result == 1L) {
            // 抢购成功，记录订单
            OrderRecord order = new OrderRecord();
            order.setOrderNo(orderNo);
            order.setProductCode(productCode);
            order.setUserId(userId);
            order.setStatus(1);
            orderRecordMapper.insert(order);
            return true;
        }
        return false;
    }
}
四、抢购流程
text
用户请求
    ↓
Controller 接收
    ↓
调用 SeckillService.seckill()
    ↓
执行 Lua 脚本（Redis）
    ├── 查询库存
    ├── 库存 > 0 → 减库存 → 返回 1
    └── 库存 ≤ 0 → 返回 0
    ↓
返回 1 → 插入订单记录 → 返回成功
返回 0 → 直接返回失败
五、Lua 脚本 vs 数据库乐观锁
对比项	Lua 脚本	数据库乐观锁
响应时间	1-5ms	10-50ms
并发能力	高（10w+ QPS）	低（几千 QPS）
数据一致性	最终一致	强一致
实现复杂度	简单	较复杂
适用场景	秒杀、抢购	普通下单
六、JMeter 压测结果
指标	数值
并发线程数	100
总请求数	1000
初始库存	10
成功请求	10
失败请求	990
错误率	0%（业务拒绝）
响应时间	< 10ms
结论：Lua 脚本保证了原子性，库存没有超卖 ✅

七、验证命令
bash
# 1. 初始化库存
SET stock:PROD001 10

# 2. 查看库存
GET stock:PROD001

# 3. 执行 Lua 脚本
EVAL "local s=redis.call('get',KEYS[1]) if tonumber(s)>0 then redis.call('decr',KEYS[1]) return 1 else return 0 end" 1 stock:PROD001

# 4. 查看剩余库存
GET stock:PROD001
八、面试速答
问题	答案
为什么用 Lua 脚本？	原子性、减少网络开销、支持逻辑判断
Redis 事务和 Lua 区别？	事务不支持条件判断，Lua 支持
抢库存怎么防超卖？	Lua 脚本：查库存 → 减库存 原子执行
一人限购一次怎么实现？	用 Set 记录已购用户，脚本中检查
九、今日产出
任务	状态
理解 Redis 事务	✅
理解 Lua 脚本原理	✅
实现抢库存 Lua 脚本	✅
Java 调用 Lua 脚本	✅
JMeter 压测验证	✅
确认无超卖	✅
text

---

**5 个 Markdown 文件已整理完成，可以直接保存到 GitHub！**