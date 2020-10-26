package kasa.test.kafka;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

/*
 * https://www.cnblogs.com/alexgl2008/articles/12411998.html
 * @author : Kasa
 * @date : 2020/10/23 17:51
 * @descripthon : TODO Flink 整合 Kafka，实现 Exactly-Once
 */
public class RestartStrategyDemo {

    public static void main(String[] args) {

        System.setProperty("HADOOP_USER_NAME", "root");

        // 1.创建流运行环境
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 请注意此处：
        //1.只有开启了CheckPointing,才会有重启策略
        env.enableCheckpointing(5000);

        env.setStateBackend(new FsStateBackend("hdfs://dev201:/flink/checkpoints"));

        //2.默认的重启策略是：固定延迟无限重启
        //此处设置重启策略为：出现异常重启3次，隔5秒一次
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(5)));
        //系统异常退出或人为 Cancel 掉，不删除checkpoint数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //设置Checkpoint模式（与Kafka整合，一定要设置Checkpoint模式为Exactly_Once）
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);


        // 2.Source:读取 Kafka 中的消息
        // Kafka props
        Properties properties = new Properties();
        // 指定Kafka的Broker地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "dev201:9092");
        // 指定组ID
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
        // 如果没有记录偏移量，第一次从最开始消费
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Kafka的消费者，不自动提交偏移量
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), properties);

        // Checkpoint成功后，还要向Kafka特殊的topic中写偏移量(此处不建议改为false )
        // 设置为false后，则不会向特殊topic中写偏移量。
        // kafkaSource.setCommitOffsetsOnCheckpoints(false);
        // 通过addSource()方式，创建 Kafka DataStream
        DataStreamSource<String> kafkaDataStream = env.addSource(kafkaSource);

        // 3.Transformation过程
        SingleOutputStreamOperator<Tuple2<String, Integer>> streamOperator = kafkaDataStream.map(str -> Tuple2.of(str, 1)).returns(Types.TUPLE(Types.STRING, Types.INT));

        // 此部分读取Socket数据，只是用来人为出现异常，触发重启策略。验证重启后是否会再次去读之前已读过的数据(Exactly-Once)
        // *************** start **************/
        DataStreamSource<String> socketTextStream = env.socketTextStream("dev201", 18888);

        SingleOutputStreamOperator<String> streamOperator1 = socketTextStream.map(new MapFunction<String, String>() {
            @Override
            public String map(String word) throws Exception {
                if ("error".equals(word)) {
                    throw new RuntimeException("Throw Exception");
                }
                return word;
            }
        });
        // ************* end **************

        //对元组 Tuple2 分组求和
        // SingleOutputStreamOperator<Tuple2<String, Integer>> sum = streamOperator.keyBy(0).sum(1);
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = streamOperator.keyBy(t2 -> t2.f0).sum(1);

        // 4.Sink过程
        sum.print();

        streamOperator1.print();

        // 5.任务执行
        try {
            env.execute("RestartStrategyDemo");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

