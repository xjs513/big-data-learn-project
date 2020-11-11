package com.atguigu.apitest;/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/4 21:23
 * @Description:
 */

import com.atguigu.modules.SensorReading;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.io.IOException;

public class SideOutputTest {

    static OutputTag<SensorReading> lateOutputTag = new OutputTag<SensorReading>("sideOutput"){};

    public static void main(String[] args) {
        // 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从调用时刻开始给 env 创建的每一个 stream 追加时间特征
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.setParallelism(1);

        // 设置状态后端
        try {
            env.setStateBackend(new RocksDBStateBackend("", true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. 从文件读取数据 nc -l -p 13579
        // String inputPath = "data/flink/sensor.txt";
        // DataStreamSource<String> inputStream = env.readTextFile(inputPath);

        // 从套接字读取数据
        DataStream<String> inputStream = env.socketTextStream("localhost", 13579);

        // 3. 做类型转换
        DataStream<SensorReading> dataStream = inputStream
                .map(line -> {
                    String[] split = line.split(",");
                    return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
                });

        SingleOutputStreamOperator<SensorReading> result = dataStream.process(new SplitTempProcessor(30.0D));

        result.print("result");

        result.getSideOutput(lateOutputTag).print("sideOutput");

        try {
            env.execute("SideOutputTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SplitTempProcessor extends ProcessFunction<SensorReading, SensorReading>{

    private double threshold;

    public SplitTempProcessor(double threshold){
        this.threshold = threshold;
    }

    @Override
    public void processElement(SensorReading sensorReading, Context context, Collector<SensorReading> collector) {
        if (sensorReading.getTemperature() > threshold){
            collector.collect(sensorReading);
        } else {
            context.output(SideOutputTest.lateOutputTag, sensorReading);
        }
    }
}