package com.atguigu.sinktest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

/**
 * @author : Kasa
 * @date : 2020/10/30 18:01
 * @descripthon :
 */
public class KafkaSinkTest {
    public static void main(String[] args) {
        // 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 从文件读取数据
        String inputPath = "data/flink/sensor.txt";
        DataStreamSource<String> inputStream = env.readTextFile(inputPath);

        // 3. 做类型转换
//        DataStream<SensorReading> dataStream = inputStream.map(line -> {
//            String[] split = line.split(",");
//            return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
//        });

        String brokerList = "dev201:9092";
        String topic = "test";

        FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(
                brokerList, topic, new SimpleStringSchema()
        );

        inputStream.addSink(producer);

        try {
            env.execute("KafkaSinkTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
