/*
 * https://nftscan.com/
 * Copyright © 2022  All rights reserved.
 */
package com.frlyh.exa.tests;

import com.frlyh.exa.tests.entity.Age;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;


/**
 * @author frlyh
 * @version 1.0: StreamTests.java, v 0.1 2022/03/21 15:57 PM  frlyh Exp $
 */
@SpringBootTest
@Slf4j
public class StreamTests {
    @Test
    public void test3() throws Exception {
        IntStream intStream=IntStream.rangeClosed(1, 10);
        //会保留过滤函数返回true的元素，此处是保留偶数
        intStream=intStream.filter(x->{
            return x%2==0;
        }).peek(x->{ //peek方法指定的函数，以流中的元素为入参，无返回值，即不会修改元素本身
            System.out.println("filter->"+x);
        });
        //对流中的所有元素执行某个修改动作，此处是将所有值加1
        intStream=intStream.map(x->{
            return x+1;
        }).peek(x->{
            System.out.println("map->"+x);
        });

        //flatMap同map，区别在于flatMap指定的函数其返回值是一个IntStream，而非一个int值，最终flatMap返回的
        //IntStream是将每次调用flatMap返回的子IntStream合并后的结果
        intStream=intStream.flatMap(x->{
            //返回IntStream时可以返回多个元素
            return IntStream.of(x+3,x+2,x+1);
        }).peek(x->{
            System.out.println("flatMap->"+x);
        });

//        System.out.println("after flatMap" +intStream);
        print("after flatMap", intStream);
    }

    @Test
    public void test() throws Exception {
        //包含指定的元素
//        IntStream intStream=IntStream.of(1);
        //返回的int流中的元素是已经排序好的
        IntStream intStream=IntStream.of(1,3,2,5,4,6);
        print("of",intStream);

        //从11到16,不包含16
        intStream=IntStream.range(11,16);
        //从11到16,包含16
//        intStream=IntStream.rangeClosed(11,16);
        print("range",intStream);

        //包含指定的元素,add方法底层也是调用accept方法，然后返回this
        //返回的int流中的元素顺序与添加顺序一致
        intStream=IntStream.builder().add(23).add(22).add(21).build();
        print("builder", intStream);

        //指定一个int生成函数
        //返回的int流中的元素不排序
        intStream=IntStream.generate(()->{
            Random random=new Random();
            return random.nextInt(100);
        }).limit(6);
        print("generate", intStream);

        //指定一个int生成函数，前一次执行函数的结果会作为下一次调用函数的入参
        //第一个参数seed就是第一次调用生成函数的入参
        //返回的int流中的元素不排序
        intStream=IntStream.iterate(1,x->{
            int a=2*x;
            if(a>16){
                return a-20;
            }else{
                return a;
            }
        }).limit(6);
        print("iterate", intStream);
    }

    @Test
    public void test2() throws Exception {
        IntStream streamA=IntStream.range(11,15);
        IntStream streamB=IntStream.range(6,10);
        //将两个IntStream 合并起来
        //返回的int流的元素顺序与添加的流的元素顺序一致，不排序
        IntStream streamC=IntStream.concat(streamA,streamB);
        print("concat", streamC);
    }

    private void print(String start, IntStream intStream){
        System.out.println("print for->"+start);
        intStream.forEach(x->{
            System.out.println(x);
        });
    }

    @Test
    public void test4() throws Exception {
        IntStream intStream=IntStream.rangeClosed(1, 3);
        intStream.mapToObj(x->{
            return new Age(x);
        }).forEach(x->{
            System.out.println("mapToObj->"+x);
        });

        //执行完mapToObj后intStream本身已经关闭了不能继续操作，只能操作其返回的新流
        //此处要继续操作就必须重新初始化一个新的IntStream
        intStream=IntStream.rangeClosed(1, 3);
        intStream.mapToLong(x->{
            return x+1;
        }).forEach(x->{
            System.out.println("mapToLong->"+x);
        });

        intStream=IntStream.rangeClosed(1, 3);
        intStream.mapToDouble(x->{
            return x+2;
        }).forEach(x->{
            System.out.println("mapToDouble->"+x);
        });

        //同上面的mapToLong，区别在于不能指定转换函数，而是采用标准的int到long类型的转换方法
        intStream=IntStream.rangeClosed(1, 3);
        intStream.asLongStream().forEach(x->{
            System.out.println("asLongStream->"+x);
        });

        intStream=IntStream.rangeClosed(1, 3);
        intStream.asDoubleStream().forEach(x->{
            System.out.println("asDoubleStream->"+x);
        });
    }
    @Test
    public void test6() throws Exception {
        IntStream intStream=IntStream.of(6,1,3,2,5,4).parallel();
        intStream.forEach(x->{
            System.out.println("forEach->"+x);
        });

        //forEachOrdered同forEach，区别在于并行流处理下，forEachOrdered会保证实际的处理顺序与流中元素的顺序一致
        //而forEach方法无法保证，默认的串行流处理下，两者无区别，都能保证处理顺序与流中元素顺序一致
        intStream=IntStream.of(6,1,3,2,5,4).parallel();
        intStream.forEachOrdered(x->{
            System.out.println("forEachOrdered->"+x);
        });
    }

    @Test
    public void test7() throws Exception {
        IntStream intStream=IntStream.of(6,1,3,2,5,4);
        OptionalInt optionalInt=intStream.reduce((x, y)->{
            System.out.println("x->"+x+",y->"+y);
            return x+y;
        });
        System.out.println("result->"+optionalInt.getAsInt());

        System.out.println("");

        intStream=IntStream.of(6,1,3,2,5,4);
        //同第一个reduce方法，区别在于可以指定起始的left，第一个reduce方法使用第一个元素作为起始的left
        int result=intStream.reduce(2,(x, y)->{
            System.out.println("x->"+x+",y->"+y);
            return x+y;
        });
        System.out.println("result->"+result+"\n");

        intStream=IntStream.of(6,1,3,2,5,4);
        //同forEach方法，首先调用supplier函数生成一个值，将该值作为accumulator函数的第一个参数，accumulator函数的第二个
        //参数就是流中的元素，注意第三个参数combiner无意义，可置为null
        result=intStream.collect(()->{
            Random random=new Random();
            return random.nextInt(10);
        },(x,y)->{
            System.out.println("ObjIntConsumer x->"+x+",y->"+y);
        },null);
        //返回值是supplier函数生成的值
        System.out.println("collect result->"+result+"\n");

    }

    @Test
    public void test8() throws Exception {
        IntStream intStream=IntStream.of(6,1,1,2,5,2,3,3,4,8,6,11,10,9);
        intStream.distinct() //对流中的元素去重
                .sorted()  //将流中的元素排序，默认升序
                .skip(3) //跳过前3个元素，此处是跳过1,2,3三个元素
                .limit(6) //限制流中元素的最大个数
                .forEach(x->{
                    System.out.println(x);
                });

    }

    @Test
    public void test9() throws Exception {
        IntStream intStream=IntStream.of(6,1,1,2,5,2,3,4);
        //取流中元素的最大值
        OptionalInt max=intStream.max();
        System.out.println("max->"+max.getAsInt());

        //同其他没有流的方法，max操作会中断流，再对该流执行相关流处理方法会报错synchronizedTest.StreamTest
        intStream=IntStream.of(6,1,1,2,5,2,3,4);
        //取流中元素的最小值
        OptionalInt min=intStream.min();
        System.out.println("max->"+max.getAsInt());

        intStream=IntStream.of(6,1,1,2,5,2,3,4);
        //取流中元素的平均值
        OptionalDouble average=intStream.average();
        System.out.println("average->"+average.getAsDouble());

        intStream=IntStream.of(6,1,1,2,5,2,3,4);
        //取流中元素的个数
        long count=intStream.count();
        System.out.println("count->"+count);

        intStream=IntStream.of(6,1,1,2,5,2,3,4);
        //取流中元素的总和
        int sum=intStream.sum();
        System.out.println("sum->"+sum);

        intStream=IntStream.of(6,1,1,2,5,2,3,4);
        //取流中元素的统计情形，即一次返回min,max,count等属性
        IntSummaryStatistics summaryStatistics=intStream.summaryStatistics();
        System.out.println(summaryStatistics.toString());
    }

}
