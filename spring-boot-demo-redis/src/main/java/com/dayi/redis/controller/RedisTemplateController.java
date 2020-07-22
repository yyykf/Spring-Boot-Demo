package com.dayi.redis.controller;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RedisController 的主控制器
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/21 11:13
 */
@RestController
@RequestMapping("/template")
public class RedisTemplateController {

    public static final String SUCCESS = "SUCCESS";

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
}
