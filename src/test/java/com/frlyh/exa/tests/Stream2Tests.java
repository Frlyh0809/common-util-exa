/*
 * https://nftscan.com/
 * Copyright © 2022  All rights reserved.
 */
package com.frlyh.exa.tests;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author frlyh
 * @version 1.0: Stream2Tests.java, v 0.1 2022/05/23 14:46 PM  frlyh Exp $
 */
@SpringBootTest
@Slf4j
public class Stream2Tests {

    @Data
    @AllArgsConstructor
    class Message {
        private String title;
        private String no;
        private int amount;
        private int month;


    }


    /**
     * 分组求和
     */
    @Test
    public void doTest_1() {

        List<Message> messages = new ArrayList<Message>();
        messages.add(new Message("嘿嘿", "1433223", 34234, 1));
        messages.add(new Message("嘿嘿", "1433223", 42344, 2));
        messages.add(new Message("嘿嘿", "1433223", 34242, 3));
        messages.add(new Message("嘿嘿", "1433223", 78675, 4));
        messages.add(new Message("嘿嘿", "1433223", 86766, 5));
        messages.add(new Message("嘿嘿", "1433223", 54567, 6));
        messages.add(new Message("嘿嘿", "1433223", 87575, 7));
        messages.add(new Message("嘿嘿", "1433223", 56756, 8));
        messages.add(new Message("嘿嘿", "1433223", 98770, 9));
        messages.add(new Message("嘿嘿", "1433223", 45455, 10));
        messages.add(new Message("嘿嘿", "1433223", 67567, 11));
        messages.add(new Message("嘿嘿", "1433223", 56475, 12));
        messages.add(new Message("哈哈", "1234567", 64543, 1));
        messages.add(new Message("哈哈", "1234567", 76578, 2));
        messages.add(new Message("哈哈", "1234567", 76656, 3));
        messages.add(new Message("哈哈", "1234567", 67787, 4));
        messages.add(new Message("哈哈", "1234567", 95664, 5));
        messages.add(new Message("哈哈", "1234567", 56567, 6));
        messages.add(new Message("哈哈", "1234567", 75663, 7));
        messages.add(new Message("哈哈", "1234567", 34564, 8));
        messages.add(new Message("哈哈", "1234567", 87645, 9));
        messages.add(new Message("哈哈", "1234567", 23543, 10));
        messages.add(new Message("哈哈", "1234567", 56453, 11));
        messages.add(new Message("哈哈", "1234567", 53464, 12));


//        Map<String,List<Message>> stringListMap = messages.stream().collect(Collectors.groupingBy(Message::getNo));
        //根据no分组
        Map<String, List<Message>> resultMap = messages.stream().collect(Collectors.groupingBy(Message::getNo));
        ;
        List<Map<String, Object>> result = new ArrayList<>();
        resultMap.entrySet().stream().forEach(entry -> {
            Map<String, Object> dataMap = new HashMap<>();
            String arr[] = entry.getKey().split("-");
            List<Message> list_one = entry.getValue();
            dataMap.put("no", arr[0]);
            //求和
            int total = list_one.stream().mapToInt(o -> Integer.parseInt(String.valueOf(o.getAmount()))).sum();
            dataMap.put("total", total);
            result.add(dataMap);
        });
        log.info("result = [{}]", JSON.toJSONString(result));

//        System.out.println(result);
    }

}
