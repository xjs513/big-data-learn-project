//package com.atguigu.sinktest;
//
//import com.atguigu.modules.SensorReading;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.datastream.DataStreamSource;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.connectors.redis.RedisSink;
//import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
//
///**
// * @author : Kasa
// * @date : 2020/10/30 18:52
// * @descripthon :
// */
//public class RedisSinkTest {
//    public static void main(String[] args) {
//        // 1. 创建执行环境
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        // 2. 从文件读取数据
//        String inputPath = "data/flink/sensor.txt";
//        DataStreamSource<String> inputStream = env.readTextFile(inputPath);
//
//        // 3. 做类型转换
//        DataStream<SensorReading> dataStream = inputStream.map(line -> {
//            String[] split = line.split(",");
//            return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
//        });
//
//        // 初始化 Redis 连接
//        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
//                .setHost("dev201")
//                .setPort(6379)
//                .setDatabase(13)
//                .build();
//        // 4. 输出数据
//        dataStream.addSink(new RedisSink<>(config, new MyRedisMapper()));
//
//        // 5. 执行
//        try {
//            env.execute("RedisSinkTest");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//// 定义一个 RedisMapper
//class MyRedisMapper implements RedisMapper<SensorReading>{
//
//    @Override
//    public RedisCommandDescription getCommandDescription() {
//        return new RedisCommandDescription(
//                RedisCommand.HSET,
//                "sensor_temperature"
//        );
//    }
//
//    @Override
//    public String getKeyFromData(SensorReading sensorReading) {
//        return sensorReading.getId();
//    }
//
//    @Override
//    public String getValueFromData(SensorReading sensorReading) {
//        return String.valueOf(sensorReading.getTemperature());
//    }
//}