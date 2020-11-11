package kasa.test.clickhouse;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

/**
 * @author : Kasa
 * @date : 2020/11/9 17:50
 * @descripthon :
 */
public class Kafka2ClickhouseDriver{
    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "root");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //设置并行度，为了方便测试，查看消息的顺序，这里设置为1，可以更改为多并行度
        env.setParallelism(1);
        //checkpoint设置
        //每隔20s进行启动一个检查点【设置checkpoint的周期】
        env.enableCheckpointing(20000);
        //设置模式为：exactly_one，仅一次语义
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //确保检查点之间有1s的时间间隔【checkpoint最小间隔】
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
        //检查点必须在10s之内完成，或者被丢弃【checkpoint超时时间】
        env.getCheckpointConfig().setCheckpointTimeout(10000);
        //同一时间只允许进行一次检查点
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //表示一旦Flink程序被cancel后，会保留checkpoint数据，以便根据实际需要恢复到指定的checkpoint
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //设置 StateBackend,将检查点保存在hdfs上面，默认保存在内存中。这里先保存到本地
       // env.setStateBackend(new FsStateBackend("file:///Users/temp/cp/"));
         // env.setStateBackend(new RocksDBStateBackend("file:///C:/Users/temp/cp/"));

        env.setStateBackend(new RocksDBStateBackend("hdfs://dev201:8020/flink/rocks_checkpoints", true));

        //2.默认的重启策略是：固定延迟无限重启
        //此处设置重启策略为：出现异常重启3次，隔5秒一次
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(5)));

        //设置kafka消费参数
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "dev201:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-test");
        //kafka分区自动发现周期
        props.put(FlinkKafkaConsumerBase.KEY_PARTITION_DISCOVERY_INTERVAL_MILLIS, "3000");
        // Kafka的消费者，不自动提交偏移量
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // 如果没有记录偏移量，第一次从最开始消费
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        /*SimpleStringSchema可以获取到kafka消息，JSONKeyValueDeserializationSchema可以获取都消息的key,value，metadata:topic,partition，offset等信息*/
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), props);
//        FlinkKafkaConsumer<ObjectNode> consumer = new FlinkKafkaConsumer<>("test", new JSONKeyValueDeserializationSchema(true), props);
        /*
         * Flink 从topic中最初的数据开始消费
         */
        consumer.setStartFromEarliest();
        //加入kafka数据源
        SingleOutputStreamOperator<String> streamSource = env.addSource(consumer).uid("dd").name("dd");
//        SingleOutputStreamOperator<ObjectNode> streamSource = env.addSource(consumer).uid("dd").name("dd");
        //数据传输到下游
        // streamSource.addSink(new MySqlTwoPhaseCommitSink()).name("MySqlTwoPhaseCommitSink").uid("MySqlTwoPhaseCommitSink");

        streamSource.print();

        //触发执行
        env.execute("Kafka2ClickhouseDriver");
    }
}
