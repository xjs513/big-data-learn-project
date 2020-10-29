package com.atguigu.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author : Kasa
 * @date : 2020/10/26 18:04
 * @descripthon : https://www.bilibili.com/video/BV19K4y1f7of?p=7
 */
public class StreamingWordCountDemo {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> elements = env.readTextFile("C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\data\\input\\words.txt");

        DataStream<Tuple2<String, Integer>> tuples = elements.flatMap(
                (FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
                    String[] strings = s.split(" ");
                    for (String string : strings) {
                        collector.collect(new Tuple2<>(string, 1));
                    }
                }
        ).returns(Types.TUPLE(Types.STRING, Types.INT));

        DataStream<Tuple2<String, Integer>> result = tuples
                .keyBy((KeySelector<Tuple2<String, Integer>, String>) tuple2 -> tuple2.f0)
                .sum(1);

        try {
            result.print();
            env.execute("StreamingWordCountDemo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
