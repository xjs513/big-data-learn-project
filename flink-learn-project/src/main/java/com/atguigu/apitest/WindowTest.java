package com.atguigu.apitest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SessionWindowTimeGapExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @author : Kasa
 * @date : 2020/11/2 14:30
 * @descripthon :
 */
public class WindowTest {
    public static void main(String[] args) {
        // 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        // 2. 从文件读取数据
        // String inputPath = "data/flink/sensor.txt";
        // DataStreamSource<String> inputStream = env.readTextFile(inputPath);
        DataStream<String> inputStream = env.socketTextStream("dev201", 13579);

        // 3. 做类型转换
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] split = line.split(",");
            return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
        });

        // 每 15 秒统计一次个传感器温度最小值
        DataStream<Tuple2<String, Double>> result =
            dataStream
                .map(sensorReading -> new Tuple2<>(sensorReading.getId(), sensorReading.getTemperature()))
                    .returns(Types.TUPLE(Types.STRING, Types.DOUBLE))
                .keyBy(tuple2 -> tuple2.f0) // 分组
                // .timeWindow(Time.seconds(15L))
                // .timeWindow(Time.seconds(15), Time.seconds(10))
                // .window(TumblingEventTimeWindows.of(Time.seconds(15L))) // 开窗  =》 分桶
                // .window(SlidingEventTimeWindows.of())
                // .window(EventTimeSessionWindows.withGap(Time.seconds(15L)))
//                .window(EventTimeSessionWindows.withDynamicGap(
//                        (SessionWindowTimeGapExtractor<Tuple2<String, Double>>) element -> element.f1.longValue()
//                ))

                // .countWindow(10L, 5L)
                //  .countWindowAll(5)
                // .timeWindowAll(Time.seconds(15L))
                // .reduce((t1, t2) -> new Tuple2<>(t1.f0, t1.f1 + t2.f1));

                .timeWindow(Time.seconds(15L))
                .reduce((currentRes, newData) -> new Tuple2<>(currentRes.f0, Math.min(currentRes.f1, newData.f1)));

        // 4. 输出数据
        result.print();
        // 5. 执行
        try {
            env.execute("JdbcSinkTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
