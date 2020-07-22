package com.dayi.redis.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 的主控制器
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/21 11:13
 */
@RestController
@RequestMapping("/template")
public class RedisController {

    public static final String SUCCESS = "SUCCESS";

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisController.class);

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * RedisCallback接口，可以在同一连接下执行多条命令
     *
     * @return 成功
     */
    @GetMapping("/redisCallback")
    public String redisCallback() {
        redisTemplate.execute((RedisCallback<Object>)redisConnection -> {
            redisConnection.set("key1".getBytes(), "value".getBytes());
            redisConnection.hSet("myHash".getBytes(), "field1".getBytes(), "value".getBytes());

            return null;
        });

        return SUCCESS;
    }

    /**
     * SessionCallback接口
     *
     * @return 成功
     */
    @GetMapping("/sessionCallback")
    public String sessionCallback() {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.opsForValue().set("key2", "value");
                redisOperations.opsForHash().put("myHash", "field2", "value");

                return null;
            }
        });

        return SUCCESS;
    }

    /**
     * String常用操作
     *
     * @return 成功
     */
    @GetMapping("/string")
    public String stringOps() {
        // RedisTemplate的Value序列化为默认JDK序列化，所以保存的时候不是整数，不可以运算
        redisTemplate.opsForValue().set("int_key", "1");
        // 使用String序列化，可运算
        stringRedisTemplate.opsForValue().set("int", "1");

        // 运算
        stringRedisTemplate.opsForValue().increment("int", 1L);
        // 使用底层Jedis连接进行减1运算，因为RedisTemplate不支持
        Jedis jedis = (Jedis)stringRedisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        jedis.decr("int");

        return SUCCESS;
    }

    /**
     * Hash常用操作
     *
     * @return 成功
     */
    @GetMapping("/hash")
    public String hashOps() {
        // 准备数据
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("field3", "value");
        hashMap.put("field4", "value");

        // 全部存入
        redisTemplate.opsForHash().putAll("myHash", hashMap);
        // 存入单个字段
        redisTemplate.opsForHash().put("myHash", "field5", "value");
        // 绑定操作
        BoundHashOperations<Object, String, Object> myHash = redisTemplate.boundHashOps("myHash");
        // 删除
        myHash.delete("field4", "field5");
        // 新增
        myHash.put("field6", "value");

        return SUCCESS;
    }

    /**
     * List常用操作
     *
     * @return 操作结果及成功标志
     */
    @GetMapping("/list")
    public Map<String, Object> listOps() {
        // 左压栈
        redisTemplate.opsForList().leftPushAll("myList", "5", "4", "3", "2", "1");
        // 右压栈
        redisTemplate.opsForList().rightPushAll("myList", "6", "7", "8", "9", "10");
        // 绑定操作
        BoundListOperations<Object, Object> myList = redisTemplate.boundListOps("myList");
        // 左弹栈
        Object left = myList.leftPop();
        // 右弹栈
        Object right = myList.rightPop();
        // 索引获取，索引0开始
        Object first = myList.index(0);
        // 链表长度
        Long size = myList.size();
        // 范围获取
        List<Object> range = myList.range(0, size - 1);

        Map<String, Object> result = Maps.newHashMap();
        result.put("success", SUCCESS);
        result.put("left", left);
        result.put("right", right);
        result.put("first", first);
        result.put("size", size);
        result.put("range", range);

        return result;
    }

    /**
     * Set常用操作
     *
     * @return 操作结果及成功标志
     */
    @GetMapping("/set")
    public Map<String, Object> setOps() {
        // Set不允许重复元素，所以第一个操作只会添加5个元素
        redisTemplate.opsForSet().add("set1", "1", "1", "2", "3", "4", "5");
        redisTemplate.opsForSet().add("set2", "2", "4", "6", "8");
        // 绑定操作
        BoundSetOperations<Object, Object> set = redisTemplate.boundSetOps("set1");
        // 添加、删除
        set.add("6", "7");
        set.remove("1", "7");
        // 成员数
        Long size = set.size();
        // 所有元素
        Set<Object> members = set.members();
        // 交集
        Set<Object> inter = set.intersect("set2");
        // 交集存储到新Set
        set.intersectAndStore("set2", "interSet");
        // 差集
        Set<Object> diff = set.diff("set2");
        // 差集存储到新Set
        set.diffAndStore("set2", "diffSet");
        // 并集
        Set<Object> union = set.union("set2");
        // 并集存储到新Set
        set.unionAndStore("set2", "unionSet");

        Map<String, Object> result = Maps.newHashMap();
        result.put("success", SUCCESS);
        result.put("size", size);
        result.put("members", members);
        result.put("inter", inter);
        result.put("diff", diff);
        result.put("union", union);

        return result;
    }

    @GetMapping("/zset")
    public Map<String, Object> zsetOps() {
        // 存放分数和值的对象，值为String类型，分数为Double类型
        Set<ZSetOperations.TypedTuple<String>> typedTupleSet = Sets.newHashSet();
        for (int i = 0; i < 10; i++) {
            ZSetOperations.TypedTuple<String> typedTuple = new DefaultTypedTuple<>("value" + i, i * 0.1);
            typedTupleSet.add(typedTuple);
        }
        // 添加元素，这里可接受的TypedTupleSet泛型取决于RedisTemplate的Value泛型
        stringRedisTemplate.opsForZSet().add("zset", typedTupleSet);
        // 绑定操作
        BoundZSetOperations<String, String> zset = stringRedisTemplate.boundZSetOps("zset");
        // 添加单个元素
        zset.add("value10", 0.111);
        // 索引范围获取，升序
        Set<String> rangeSet = zset.range(0, 5);
        // 索引范围获取，带分数，升序
        Set<ZSetOperations.TypedTuple<String>> rangeSetWithScores = zset.rangeWithScores(0, 5);
        // 索引范围获取，降序
        Set<String> reverseRange = zset.reverseRange(0, 5);
        // 分数范围获取
        Set<String> scoreSet = zset.rangeByScore(0.1, 0.6);
        // 分数范围获取，带分数，升序
        Set<ZSetOperations.TypedTuple<String>> scoreSetWithScores = zset.rangeByScoreWithScores(0.1, 0.6);
        // 按字符串范围排序获取，大于value3小于等于value8
        RedisZSetCommands.Range range = new RedisZSetCommands.Range();
        range.gt("value3").lte("value8");
        Set<String> lexSet = zset.rangeByLex(range);
        // 删除
        zset.remove("value9", "value2");
        // 获取分数
        Double score = zset.score("value8");

        Map<String, Object> result = Maps.newHashMap();
        result.put("success", SUCCESS);
        result.put("rangSet", rangeSet);
        result.put("rangeSetWithScores", rangeSetWithScores);
        result.put("reverseRange", reverseRange);
        result.put("scoreSet", scoreSet);
        result.put("scoreSetWithScores", scoreSetWithScores);
        result.put("lexSet", lexSet);
        result.put("score", score);

        return result;
    }

    /**
     * Redis事务
     *
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/multi")
    public Map<String, Object> multiOps() {
        // 设置待监控的key
        redisTemplate.opsForValue().set("multiKey", "multi");
        // 使用同一Redis连接
        List list = (List)redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 监控Key变化
                operations.watch("multiKey");
                // 开启事务，命令进行队列，暂不执行
                operations.multi();
                operations.opsForValue().set("multiKey1", "value");
                operations.opsForValue().set("multiKey2", "value");
                // 获取值为null，因为命令还在队列中未执行
                Object multiKey1 = operations.opsForValue().get("multiKey1");
                Object multiKey2 = operations.opsForValue().get("multiKey2");
                System.out.println("multiKey1：" + multiKey1);
                System.out.println("multiKey2：" + multiKey2);
                // 将抛异常，但不影响事务的执行
                // operations.opsForValue().increment("multiKey1");

                // 检测监控的key是否被修改，未修改执行命令，修改取消事务，可以使用断点进行测试
                return operations.exec();
            }
        });

        Map<String, Object> result = Maps.newHashMap();
        result.put("success", SUCCESS);
        // 存放的是每条命令返回的结果，分别为 true、true、value、value
        result.put("list", list);

        return result;
    }

    /**
     * Redis流水线
     *
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    @GetMapping("pipline")
    public Map<String, Object> pipline() {
        long startTime = System.currentTimeMillis();
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 插入10万条数据
                for (int i = 0; i < 100000; i++) {
                    operations.opsForValue().set("pipline:" + i, i);
                    // 获取值为null
                    Object value = operations.opsForValue().get("pipline" + i);
                    Assert.isNull(value, "value应该为null");
                    // 设置一分钟过期
                    operations.expire("pipline:" + i, 1L, TimeUnit.MINUTES);

                }

                return null;
            }
        });
        long endTime = System.currentTimeMillis();

        Map<String, Object> result = Maps.newHashMap();
        result.put("success", SUCCESS);
        result.put("totalTime", (endTime - startTime));

        return result;
    }

    /**
     * Redis 简单Lua脚本，无参数
     *
     * @return 操作结果
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/luaWithoutArgs")
    public Map<String, Object> luaWithoutArgs() {
        // 脚本内容 return 'Hello Redis'（可以使用 script.setScriptText()）
        // 脚本返回类型 String（可以使用 script.setResultType()）
        RedisScript script = RedisScript.of("return 'Hello Redis'", String.class);
        // String序列化器
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        // 执行Lua脚本，参数和结果都使用String序列化，待操作的参数和结果都为null
        String executeResult = redisTemplate.execute(script, stringSerializer, stringSerializer, null);

        Map<String, Object> result = Maps.newHashMap();
        result.put("success", SUCCESS);
        result.put("executeResult", executeResult);

        return result;
    }

    /**
     * 执行带有参数的Lua脚本
     * 
     * @param key1
     *            待操作的键1
     * @param key2
     *            待操作的键2
     * @param value1
     *            键1对应的值
     * @param value2
     *            键2对应的值
     * @return 操作结果
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/luaWithArgs")
    public Map<String, Object> luaWithArgs(String key1, String key2, String value1, String value2) {
        // @formatter:off
        // 需要参数的Lua脚本
        String lua = "redis.call('set', KEYS[1], ARGV[1]) \n"
                + "redis.call('set', KEYS[2], ARGV[2]) \n"
                + "local str1 = redis.call('get', KEYS[1]) \n"
                + "local str2 = redis.call('get', KEYS[2]) \n"
                + "if str1 == str2 then \n"
                + "return 1 \n"
                + "end \n"
                + "return 0";
        // @formatter:on
        LOGGER.info("Lua脚本：{}", lua);

        RedisScript script = RedisScript.of(lua, Long.class);
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        // 定义key
        List<Object> keys = Lists.newArrayList(key1, key2);
        // 执行Lua脚本
        Object executeResult = redisTemplate.execute(script, stringSerializer, stringSerializer, keys, value1, value2);

        Map<String, Object> result = Maps.newHashMap();
        result.put("success", SUCCESS);
        result.put("executeResult", executeResult);

        return result;
    }
}
