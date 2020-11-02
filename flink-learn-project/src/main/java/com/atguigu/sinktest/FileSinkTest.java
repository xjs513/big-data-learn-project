package com.atguigu.sinktest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;

/**
 * @author : Kasa
 * @date : 2020/10/30 17:40
 * @descripthon :
 */
public class FileSinkTest {
    public static void main(String[] args) {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 从文件读取数据
        String inputPath = "data/flink/sensor.txt";
        DataStreamSource<String> inputStream = env.readTextFile(inputPath);

//        DataStream<Tuple3<String, Long, Double>> sensorReadings = inputStream.map(line -> {
//            String[] split = line.split(",");
//            return new Tuple3<>(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
//        }).returns(Types.TUPLE(Types.STRING, Types.LONG, Types.DOUBLE));
//
//        sensorReadings.writeAsCsv("data/flink/out/out.txt");

        DataStream<SensorReading> sensorReadings = inputStream.map(line -> {
            String[] split = line.split(",");
            return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
        });

        sensorReadings.addSink(
            StreamingFileSink.forRowFormat(
                    new Path("data/flink/out/out_2"),
                    new SimpleStringEncoder<SensorReading>()
            ).build()
        );

        try {
            env.execute("FileSinkTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
