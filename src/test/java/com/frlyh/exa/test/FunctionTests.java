/*
 * https://nftscan.com/
 * Copyright © 2022  All rights reserved.
 */
package com.frlyh.exa.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * 利用Function.identity() 转换容器
 *
 * @author frlyh
 * @version 1.0: FunctionTests.java, v 0.1 2022/05/23 13:40 PM  frlyh Exp $
 */
@SpringBootTest
@Slf4j
public class FunctionTests {

    /**
     * 一、Function.identity()简单介绍
     * 当我们使用Stream时，要将它转换成其他容器或Map。这时候，就会使用到Function.identity()。
     * Function是一个接口，那么Function.identity()是什么意思呢？解释如下：
     * <p>
     * Java 8允许在接口中加入具体方法。接口中的具体方法有两种，default方法和static方法，identity()就是Function接口的一个静态方法。
     * Function.identity()返回一个输出跟输入一样的Lambda表达式对象，等价于形如t -> t形式的Lambda表达式。
     * <p>
     * identity()方法JDK源码如下：
     * <p>
     * static  Function identity() {
     * return t -> t;
     * }
     */
    @Test
    public void doTest_1() {
        Stream<String> stream = Stream.of("This", "is", "a", "test");
        Map<String, Integer> map = stream.collect(toMap(identity(), String::length));
        log.info("map = [{}]", map);

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Task {
        String title;
    }

    private static Map<String, Task> taskMap1(List<Task> tasks) {
        return tasks.stream().collect(toMap(Task::getTitle, task -> task));
    }

    private static Map<String, Task> taskMap2(List<Task> tasks) {
        return tasks.stream().collect(toMap(Task::getTitle, identity()));
    }

    @Test
    public void doTest_2() {
        List<Task> taks = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            taks.add(new Task("title-" + i));
        }
        Map<String, Task> stringTaskMap = taskMap1(taks);
        log.info("stringTaskMap = [{}]", stringTaskMap);
        Map<String, Task> stringTaskMap1 = taskMap2(taks);
        log.info("stringTaskMap1 = [{}]", stringTaskMap1);
    }

    @Test
    public void doTest_3() {
        Map<String, String> collect = Arrays.asList("a", "b", "c")
                .stream()
                .map(identity()) // <- This,
                .map(str -> str)          // <- is the same as this.
                .collect(toMap(
                        identity(), // <-- And this,
                        str -> str));
        log.info("collect = [{}]", collect);

//        Stream<String> stringStream = Arrays.asList("a", "b", "c")
//                .stream()
//                .map(identity());
//        log.info("stringStream = [{}]",stringStream);
//        Stream<String> stringStream1 = Arrays.asList("a", "b", "c")
//                .stream()
//                .map(identity()) // <- This,
//                .map(str -> str);
//        log.info("stringStream1 = [{}]",stringStream1);
    }

    /**
     * 为什么要使用Function.identity()代替str->str呢？它们有什么区别呢？
     * <p>
     * 在上面的代码中str -> str和Function.identity()是没什么区别的因为它们都是t->t。但是我们有时候不能使用Function.identity，看下面的例子：
     * <p>
     * List list = new ArrayList<>();
     * list.add(1);
     * list.add(2);
     * 下面这段代码可以运行成功：
     * <p>
     * int[] arrayOK = list.stream().mapToInt(i -> i).toArray();
     * <p>
     * 但是如果你像下面这样写：
     * <p>
     * int[] arrayProblem = list.stream().mapToInt(Function.identity()).toArray();
     * <p>
     * 运行的时候就会错误，因为mapToInt要求的参数是ToIntFunction类型，但是ToIntFunction类型和Function没有关系。
     */
    @Test
    public void doTest_4() {
        List<String> list = Lists.newArrayList("1", "2", "3");
        List<Integer> list2 = Lists.newArrayList(1, 2, 3);
        int[] arrayOK = list2.stream().mapToInt(i -> i).toArray();
        log.info("arrayOK =[{}]", arrayOK);

//        int[] arrayProblem = list2.stream().mapToInt(Function.identity()).toArray();

    }
}
