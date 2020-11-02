package com.atguigu.apitest;

import com.atguigu.modules.SensorReading;
import lombok.*;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.netty4.io.netty.bootstrap.BootstrapConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : Kasa
 * @date : 2020/10/29 16:31
 * @descripthon : 采集温度传感器数据
 */


public class SourceTest {
    public static void main(String[] args) {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 1. 从集合中读取数据
//        List<SensorReading> list = new ArrayList<>();
//        list.add(new SensorReading("sensor_1",  1547718199, 35.8));
//        list.add(new SensorReading("sensor_6",  1547718201, 15.4));
//        list.add(new SensorReading("sensor_7",  1547718202, 6.7));
//        list.add(new SensorReading("sensor_10", 1547718205, 38.1));
//        DataStream<SensorReading> stream1 = env.fromCollection(list);

        // 2. 从文件读取数据
//        String inputPath = "data/flink/sensor.txt";
//        DataStreamSource<String> stream1 = env.readTextFile(inputPath);
//        stream1.print("stream1:");

        // 3. 从 Kafka 读取数据
//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "dev201:9092");
//        properties.setProperty("group.id", "test1");
//
//        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), properties);
        /*
         * Flink 从topic中最初的数据开始消费
         */
//        consumer.setStartFromEarliest();

        /*
         * Flink从topic中最新的数据开始消费
         */
//         consumer.setStartFromLatest();

        // 这里是普世的添加数据源的方法
//        DataStream<String> stream3 = env.addSource(consumer);

//        stream3.print();
//
        // 4. 使用自定义数据源
        DataStream<SensorReading> lines = env.addSource(new MySensorSource()).setParallelism(1);

        lines.print();

        try {
            env.execute("SourceTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MySensorSource extends RichSourceFunction<SensorReading>{

    // 定义控制标志变量
    private boolean running = true;

    // 定义随机数发生器
    private Random random ;
    // 随机生成一组(10个) 传感器温度
    private List<Tuple2<String, Double>> curTemp;

    @Override
    public void open(Configuration parameters) throws Exception {
        random = new Random();
        curTemp = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            curTemp.add(new Tuple2<>("sensor_" + i, 100 * random.nextDouble()));
        }
    }

    @Override
    public void run(SourceContext<SensorReading> ctx) throws Exception {
        while (running){
            // 在上次数据基础上更新数据
            curTemp = curTemp.stream().map(t -> new Tuple2<>(t._1, t._2 + random.nextGaussian())).collect(Collectors.toList());
            TimeUnit.MILLISECONDS.sleep(1500L);
            curTemp.forEach(
                t -> ctx.collect(
                    new SensorReading(t._1, System.currentTimeMillis(), t._2 < -273.15?-273.15:t._2)
                )
            );
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}

class SimpleSource implements SourceFunction<SensorReading> {

    // 定义控制标志变量
    private boolean running = true;

    // 定义随机数发生器
    private Random random  = new Random();
    // 随机生成一组(10个) 传感器温度
    private List<Tuple2<String, Double>> curTemp = new ArrayList<>(10);


    @Override
    public void run(SourceContext<SensorReading> ctx) throws Exception {
        while (running)
            ctx.collect(
                new SensorReading("sensor_" + random.nextInt(), System.currentTimeMillis(), random.nextGaussian())
            );
    }

    @Override
    public void cancel() {
        running = false;
    }
}