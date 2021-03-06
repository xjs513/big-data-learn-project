package kasa.test.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author : Kasa
 * @date : 2020/10/15 15:04
 * @descripthon :
 */
public class KafkaSourceDemo {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "dev201:9092");
        // only required for Kafka 0.8
        // properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "demo-test");

        // 如果没有记录偏移量，第一次从最开始消费
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Kafka的消费者，不自动提交偏移量
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("em_logs_pro", new SimpleStringSchema(), properties);
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("em_logs_pro", new SimpleStringSchema(), properties);

        /*
         * Map<KafkaTopicPartition, Long> Long参数指定的offset位置
         * KafkaTopicPartition构造函数有两个参数，第一个为topic名字，第二个为分区数
         * 获取offset信息，可以用过Kafka自带的kafka-consumer-groups.sh脚本获取
         */
//        Map<KafkaTopicPartition, Long> offsets = new HashMap<>();
//        offsets.put(new KafkaTopicPartition("maxwell_new", 0), 11111111L);
//        offsets.put(new KafkaTopicPartition("maxwell_new", 1), 222222L);
//        offsets.put(new KafkaTopicPartition("maxwell_new", 2), 33333333L);

        /*
         * Flink 从topic中最初的数据开始消费
         */
        consumer.setStartFromEarliest();

        /*
         * Flink从topic中指定的时间点开始消费，指定时间点之前的数据忽略
         * 2020-11-03 14:55:00
         */
        // consumer.setStartFromTimestamp(1604386500000L);

        /*
         * Flink从topic中指定的offset开始，这个比较复杂，需要手动指定offset
         */
        //consumer.setStartFromSpecificOffsets(offsets);

        /*
         * Flink从topic中最新的数据开始消费
         */
        // consumer.setStartFromLatest();

        /*
        * Flink从topic中指定的group上次消费的位置开始消费，所以必须配置group.id参数
        */
        // consumer.setStartFromGroupOffsets();

        DataStream<String> lines = env.addSource(consumer);


         lines
//                 .filter(s -> s.contains("baobao"))
                 .print();

//        lines.map(line -> {
//            int a = 10;
//        });

        try {
            env.execute("KafkaSourceDemo");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
