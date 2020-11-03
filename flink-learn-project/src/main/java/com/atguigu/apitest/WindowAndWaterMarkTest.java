package com.atguigu.apitest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

/**
 * @author : Kasa
 * @date : 2020/11/2 14:30
 * @descripthon :
 */
public class WindowAndWaterMarkTest {
    public static void main(String[] args) {
        // 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从调用时刻开始给 env 创建的每一个 stream 追加时间特征
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.setParallelism(1);

        // 2. 从文件读取数据
        // String inputPath = "data/flink/sensor.txt";
        // DataStreamSource<String> inputStream = env.readTextFile(inputPath);
        DataStream<String> inputStream = env.socketTextStream("dev201", 13579);

        // 3. 做类型转换
        DataStream<Tuple3<String, Long, Double>> dataStream = inputStream
                .map(line -> {
                    String[] split = line.split(",");
                    return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
                })
                .map(sensorReading -> new Tuple3<>(sensorReading.getId(), sensorReading.getTimestamp(), sensorReading.getTemperature()))
                .returns(Types.TUPLE(Types.STRING, Types.LONG, Types.DOUBLE))
                .assignTimestampsAndWatermarks(
                        new BoundedOutOfOrdernessTimestampExtractor<Tuple3<String, Long, Double>>(Time.seconds(3)) {
                            @Override
                            public long extractTimestamp(Tuple3<String, Long, Double> element) {
                                return element.f1;
                            }
                        }
                );

        OutputTag<Tuple3<String, Long, Double>> lateOutputTag = new OutputTag<Tuple3<String, Long, Double>>("late"){};

        SingleOutputStreamOperator<Tuple3<String, Long, Double>> result = dataStream.keyBy(tuple3 -> tuple3.f0)
                .timeWindow(Time.seconds(15))
                .allowedLateness(Time.minutes(1))
                .sideOutputLateData(lateOutputTag)
                .reduce(
                        (currentRes, newData) -> new Tuple3<>(currentRes.f0, newData.f1, Math.min(currentRes.f2, newData.f2))
                );

        DataStream<Tuple3<String, Long, Double>> late = result.getSideOutput(lateOutputTag);

        late.print("late");
        result.print("result");

        // 5. 执行
        try {
            env.execute("JdbcSinkTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
//                .assignTimestampsAndWatermarks(
//                        new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(5L)) {
//                            @Override
//                            public long extractTimestamp(SensorReading element) {
//                                return element.getTimestamp() * 1000L;
//                            }
//                        }
//                );