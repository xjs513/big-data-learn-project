package com.atguigu.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author : Kasa
 * @date : 2020/10/26 18:04
 * @descripthon : https://www.bilibili.com/video/BV19K4y1f7of?p=7
 */
public class StreamingWordCountSocket {
    // nc -lk 13579
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置默认并行度
        env.setParallelism(8);

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        String host = parameterTool.get("host");
        int port = parameterTool.getInt("host");

        DataStream<String> elements = env.socketTextStream("dev201", 13579);

        DataStream<Tuple2<String, Integer>> tuples = elements.flatMap(
                (FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
                    String[] strings = s.split(" ");
                    for (String string : strings) {
                        collector.collect(new Tuple2<>(string, 1));
                    }
                }
        ).setParallelism(3).returns(Types.TUPLE(Types.STRING, Types.INT));

        DataStream<Tuple2<String, Integer>> result = tuples
                .keyBy((KeySelector<Tuple2<String, Integer>, String>) tuple2 -> tuple2.f0)
                .sum(1).setParallelism(3);

        try {
            result.print();
            env.execute("StreamingWordCountSocket");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
