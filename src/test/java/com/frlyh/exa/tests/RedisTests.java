/*
 * https://nftscan.com/
 * Copyright © 2022  All rights reserved.
 */
package com.frlyh.exa.tests;

import com.frlyh.exa.tests.entity.Age2;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author frlyh
 * @version 1.0: RedisTests.java, v 0.1 2022/03/21 15:40 PM  frlyh Exp $
 */
@SpringBootTest
@Slf4j
public class RedisTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void stringRedisTest() {
        final ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations
                .set("key1", "value1", Duration.ofMinutes(1));

        final Object value = valueOperations.get("key1");

        Assert.isTrue(Objects.equals("value1", value), "set失败");

        final HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("hash1", "f1", "v1");
        hashOperations.put("hash1", "f2", "v2");

        hashOperations.values("hash1").forEach(System.out::println);
    }
    @Test
    public void redisCallbackTest() {
        redisTemplate.execute((RedisCallback) connection -> {
            connection.set("rkey1".getBytes(), "rv1".getBytes());
            connection.set("rkey2".getBytes(), "rv2".getBytes());
            return null;
        });
    }

    @Test
    public void sessionCallbackTest() {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                final ListOperations listOperations = operations.opsForList();
                listOperations.leftPush("sk1", "sv1");
                listOperations.leftPush("sk1", "sv2");
                listOperations.getOperations().expire("sk1", 1, TimeUnit.MINUTES);

                listOperations.range("sk1", 0, 2).forEach(System.out::println);
                return 1;
            }
        });
    }

    @Test
    public void stringTest() {
        redisTemplate.opsForValue().set("stringKey1", "value1", 5, TimeUnit.MINUTES);

        //字符串类型的整数，不能进行数字运算；
        redisTemplate.opsForValue().set("stringKey2", "1", 5, TimeUnit.MINUTES);

        //进行数字运算，增加，减少
        redisTemplate.opsForValue().set("stringKey3", 1, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().increment("stringKey3",1);
        redisTemplate.opsForValue().decrement("stringKey3",1);

        //其它操作方法
        final Long keySize = redisTemplate.opsForValue().size("stringKey1");
        System.out.println(keySize);

        //批量设置
        Map<String,Long> map = new HashMap<>(4);
        map.put("sk1",1L);
        map.put("sk2",2L);
        map.put("sk3",3L);
        map.put("sk4",4L);
        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.opsForValue().multiSetIfAbsent(map);
        //批量获取
        redisTemplate.opsForValue().multiGet(map.keySet()).forEach(System.out::println);


        //getAndSet
        final Object sk5Value = redisTemplate.opsForValue().getAndSet("sk5", 100);
        System.out.println("sk5Value:"+sk5Value);

        redisTemplate.opsForValue().append("sk5","hello redis");
        System.out.println("sk5Value2:"+redisTemplate.opsForValue().get("sk5"));

        //按照情况设置，可以省去了之前查询出来之后判断是否存在再操作的代码；
        redisTemplate.opsForValue().setIfAbsent("sk6",1000,5,TimeUnit.MINUTES);
        redisTemplate.opsForValue().setIfPresent("sk6",100,5,TimeUnit.MINUTES);

    }

    @Test
    public void listTest() {

        stringRedisTemplate.opsForList().leftPush("lk1","lkv1");
        stringRedisTemplate.opsForList().leftPushAll("lk2","lk2v1","lk2v2");
        stringRedisTemplate.opsForList().leftPushAll("lk2",Arrays.asList("lk2v3","lk2v4"));
        stringRedisTemplate.opsForList().leftPushIfPresent("lk3","lk3v1");

        final List<String> lk2ValuesList = stringRedisTemplate.opsForList().range("lk2", 0, 3);
        System.out.println(lk2ValuesList);
    }
    @Test
    public void setTest() {
        stringRedisTemplate.opsForSet().add("sk1","sk1v1","sk1v2","sk1v3");
        stringRedisTemplate.opsForSet().add("sk2","sk1v1","sk2v2","sk2v3");

        final Set<String> sk1 = stringRedisTemplate.opsForSet().members("sk1");
        final Set<String> sk2 = stringRedisTemplate.opsForSet().members("sk2");

        System.out.println("sk1: "+sk1);
        System.out.println("sk2: "+sk2);

        final Set<String> intersect = stringRedisTemplate.opsForSet().intersect("sk1", "sk2");
        System.out.println("交集是：" + intersect);

        final Set<String> union = stringRedisTemplate.opsForSet().union("sk1", "sk2");
        System.out.println("并集：" + union);

        final Set<String> difference = stringRedisTemplate.opsForSet().difference("sk1", "sk2");
        System.out.println("差集："+ difference);

        final Long size = stringRedisTemplate.opsForSet().size("sk1");

        System.out.println("size for sk1 : " + size);

        stringRedisTemplate.delete("sk1");
        stringRedisTemplate.delete("sk2");

    }

    @Test
    public void doSetTest1() {
//        IntStream.rangeClosed(1,10).forEach(i->{
//            Age2 age2 = new Age2(i, "name" + i);
//            redisTemplate.opsForSet().add("sk1",age2);
//        });

        List<Age2> age2List= Lists.newArrayList();
        for(int i=0;i<10;i++){
            Age2 age2 = new Age2(+i, "nameA" + i);
            age2List.add(age2);

        }
        age2List.forEach(age2 -> {
            redisTemplate.opsForSet().add("sk1",age2);
        });


        List<Age2> sk1 = redisTemplate.opsForSet().pop("sk1", 2);
        System.out.println("sk1 = " + sk1);

    }

    @Test
    public void zsetTest() {



        IntStream.rangeClosed(1,100).forEach(i->{
            stringRedisTemplate.opsForZSet().add("zsk1",String.valueOf(i),i*10);
        });
        final Set<ZSetOperations.TypedTuple<String>> typedTupleSet = IntStream.rangeClosed(1, 100).mapToObj(i -> new DefaultTypedTuple<String>(String.valueOf(i), (double) i * 11)).collect(Collectors.toSet());
        stringRedisTemplate.opsForZSet().add("zsk2",typedTupleSet);

        final Set<String> zsk1 = stringRedisTemplate.opsForZSet().rangeByLex("zsk1", RedisZSetCommands.Range.range().gte(20).lte(100));
        System.out.println("范围内的集合：" + zsk1);

    }
    @Test
    public void doZsetTest1(){
//        IntStream.rangeClosed(1,10).forEach(i ->{
//            Age2 age = new Age2(i, "name"  + (100+i));
//
//            redisTemplate.opsForZSet().add("doZsetTest1",age,100+i);
////            redisTemplate.opsForZSet().add("doZsetTest1","A2"+i,300+i);
//        });
        Set res = redisTemplate.opsForZSet().reverseRangeByScore("doZsetTest1", 1, 3);
        System.out.println("res = " + res);

        Set res2 = redisTemplate.opsForZSet().reverseRange("doZsetTest1", 1, 3);

        System.out.println("res2 = " + res2);

        Set<Age2> res3 = redisTemplate.opsForZSet().reverseRange("doZsetTest1", 0, 3);
        System.out.println("res3 = " + res3);


        Long test1 = redisTemplate.opsForZSet().removeRange("doZsetTest1", 0, 3);
        System.out.println("test1 = " + test1);
        IntStream.rangeClosed(20,30).forEach(i ->{
//            Age2 age = new Age2(i, "name"  + (100+i));
//            redisTemplate.opsForZSet().removeRange("doZsetTest1",);

            //修改分数
//            redisTemplate.opsForZSet().incrementScore()


            //intersectAndStore(K key, K otherKey, K destKey) 获取2个变量的交集存放到第3个变量里面。
//            redisTemplate.opsForZSet().intersectAndStore()
//

//            redisTemplate.opsForZSet().add("doZsetTest1",age,100+i);
//            redisTemplate.opsForZSet().add("doZsetTest1","A2"+i,300+i);
        });

        //TODO  rangeByLex???
//        final Set<String> zsk1 = redisTemplate.opsForZSet().rangeByLex("name", RedisZSetCommands.Range.range().gte(0).lte(-1));
//        final Set<String> zsk3 = redisTemplate.opsForZSet().rangeByLex("doZsetTest1", RedisZSetCommands.Range.range().gte(0).lte(-1));
//        final Set<String> zsk2 = redisTemplate.opsForZSet().rangeByLex("number", RedisZSetCommands.Range.range().gte(1).lte(2));
//
//        final Set<String> zsk5 = redisTemplate.opsForZSet().rangeByLex("number", RedisZSetCommands.Range.range().gt("A11").lt("A21"));
//        final Set<String> zsk4 = redisTemplate.opsForZSet().rangeByLex("doZsetTest1", RedisZSetCommands.Range.range().gt("A11").lt("A21"));
//        System.out.println("范围内的集合：" + zsk1);

//        Long rank = redisTemplate.opsForZSet().rank(1, 5);
//        System.out.println("rank = " + rank);

    }

    @Test
    public void doZsetTest2(){
        for (int time=0;time<10;time++){
            log.info("time = [{}]",time);
            IntStream.rangeClosed(1,10).forEach(i ->{
                new Age2(i,"name"+i);
            });
//            stringRedisTemplate.opsForZSet().add("zsk1",String.valueOf(i),i*10);

        }

    }

    @Test
    public void hashTest() {
        stringRedisTemplate.opsForHash().put("hashk1","k1","v1");
        stringRedisTemplate.opsForHash().put("hashk1","k2","v1");
        stringRedisTemplate.opsForHash().put("hashk1","k3","v1");

        stringRedisTemplate.opsForHash().putIfAbsent("hashk1","k4","new V1");

        final List<Object> multiGet = stringRedisTemplate.opsForHash().multiGet("hashk1", Arrays.asList("k1", "k2"));
        System.out.println("一次获取多个：" + multiGet);

    }


}
