package com.atguigu.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.util.keys.KeySelectorUtil;

/**
 * @author : Kasa
 * @date : 2020/10/26 18:04
 * @descripthon : https://www.bilibili.com/video/BV19K4y1f7of?p=6
 */
public class BatchWordCountDemo {
    public static void main(String[] args) {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<String> elements = env.readTextFile("C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\data\\input\\words.txt");

        DataSet<Tuple2<String, Integer>> tuples = elements.flatMap(
                (FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
                    String[] strings = s.split(" ");
                    for (String string : strings) {
                        collector.collect(new Tuple2<>(string, 1));
                    }
                }
        ).returns(Types.TUPLE(Types.STRING, Types.INT));

        // 注意: 要使用aggregate，只能使用字段索引名或索引名称来进行分组groupBy(0)，否则会报一下错误:
        // 这个可以
//        DataSet<Tuple2<String, Integer>> result = tuples
//                .groupBy(0)
//                .sum(1);

        // 这个不可以
        // 异常: java.lang.UnsupportedOperationException: Aggregate does not support grouping with KeySelector functions, yet.
//        DataSet<Tuple2<String, Integer>> result = tuples
//                .groupBy((KeySelector<Tuple2<String, Integer>, String>) tuple2 -> tuple2.f0)
//                .sum(1);

        // 这个可以
        DataSet<Tuple2<String, Integer>> result = tuples
                .groupBy(0)
                .aggregate(Aggregations.SUM, 1);


        try {
            result.print();
            // env.execute("BatchWordCountDemo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
