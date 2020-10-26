package kasa.test.kafka;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author : Kasa
 * @date : 2020/10/26 18:04
 * @descripthon :
 */
public class WordCountDemo {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Integer> elements = env.fromElements(1, 2, 3);

        try {
            elements.print();
            env.execute("WordCountDemo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
