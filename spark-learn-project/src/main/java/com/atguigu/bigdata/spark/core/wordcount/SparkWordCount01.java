package com.atguigu.bigdata.spark.core.wordcount;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author : Kasa
 * @date : 2020/10/21 11:13
 * @descripthon :
 */
public class SparkWordCount01 {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[*]").setAppName("SparkWordCount01(java)");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        JavaRDD<String> lines = sc.textFile("data\\input");

        JavaPairRDD<String, Integer> counts = lines
                //每一行都分割成单词，返回后组成一个大集合
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                //key是单词，value是1
                .mapToPair(word -> new Tuple2<>(word, 1))
                //基于key进行reduce，逻辑是将value累加
                .reduceByKey((a, b) -> a + b);

        List<Tuple2<String, Integer>> tuple2s = counts.takeOrdered(5, MyTupleComparator.INSTANCE);


        System.out.println(tuple2s.size());

        tuple2s.forEach(tuple2 -> System.out.println(tuple2._1() + " = " + tuple2._2()));
    }

    //自定义比较器类
    static class MyTupleComparator implements Comparator<Tuple2<String, Integer>>,Serializable {
        final static MyTupleComparator INSTANCE = new MyTupleComparator();
        @Override
        public int compare(Tuple2<String, Integer> o1, Tuple2<String, Integer> o2) {
            return -o1._2.compareTo(o2._2);   //返回TopN
            //return o1._2.compareTo(o2._2);   //返回bottom N
        }

    }

}
