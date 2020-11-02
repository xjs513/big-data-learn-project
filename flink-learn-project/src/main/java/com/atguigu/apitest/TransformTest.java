package com.atguigu.apitest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Kasa
 * @date : 2020/10/30 10:25
 * @descripthon :
 */
public class TransformTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 2. 从文件读取数据
        String inputPath = "data/flink/sensor.txt";
        DataStream<String> inputStream = env.readTextFile(inputPath);

        DataStream<SensorReading> sensorReadings = inputStream.map(line -> {
            String[] split = line.split(",");
            return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
        });

//        KeyedStream<SensorReading, String> keyedStream = sensorReadings.keyBy(SensorReading::getId);

        // 分组集合, 输出每个传感器的最小温度值
        // DataStream<SensorReading> minBy = keyedStream.minBy("temperature");

        // 输出当前最小的温度值以及最大的时间戳 -》 reduce
//        DataStream<SensorReading> reduce = keyedStream.reduce((sr1, sr2) -> {
//            double minTemperature = sr1.getTemperature();
//            long maxTimestamp = sr1.getTimestamp();
//            if (minTemperature > sr2.getTemperature())
//                minTemperature = sr2.getTemperature();
//            if (maxTimestamp < sr2.getTimestamp())
//                maxTimestamp = sr2.getTimestamp();
//            return new SensorReading(sr1.getId(), maxTimestamp, minTemperature);
//        });
//
//        reduce.print();

        // 多流转换操作 分流 已过时, 推荐用侧输出流代替
        SplitStream<SensorReading> splitStream = sensorReadings.split(sr -> {
            List<String> list = new ArrayList<>();
            if (sr.getTemperature() > 30.0) {
                list.add("high");
            } else {
                list.add("low");
            }
            return list;
        });

        DataStream<SensorReading> highStream = splitStream.select("high");
        DataStream<SensorReading> lowStream = splitStream.select("low");
//        DataStream<SensorReading> all = splitStream.select("low", "high");
//
//        high.print("high");
//        low.print("low");
//        all.print("all");

        // 合流 connect

        DataStream<Tuple2<String, Double>> warningStream = highStream
                .map(sr -> new Tuple2<>(sr.getId(), sr.getTemperature()))
                .returns(Types.TUPLE(Types.STRING, Types.DOUBLE));

        ConnectedStreams<Tuple2<String, Double>, SensorReading> connectedStreams = warningStream.connect(lowStream);

        DataStream<Tuple3<String, Double, String>> resultStream = connectedStreams.map(
                new CoMapFunction<Tuple2<String, Double>, SensorReading, Tuple3<String, Double, String>>() {
                    @Override
                    public Tuple3<String, Double, String> map1(Tuple2<String, Double> value) {
                        return new Tuple3<>(value.f0, value.f1, "warning");
                    }

                    @Override
                    public Tuple3<String, Double, String> map2(SensorReading value) {
                        return new Tuple3<>(value.getId(), 0.0, "healthy");
                    }
                }
        ).returns(Types.TUPLE(Types.STRING, Types.DOUBLE, Types.STRING));

        resultStream.print("coMap");

        try {
            env.execute("TransformTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
