package kasa.test.kafka.tomysql.demo1;

import lombok.val;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/5 21:18
 * @Description:
 */
public class DriverDemo1 {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 读取mysql数据，获取dataStream后可以做逻辑处理，这里没有做
        DataStreamSource<Tuple2<String, String>> dataStream = env.addSource(new JdbcReader());
        //写入mysql
        dataStream.addSink(new JdbcWriter());
        //运行程序
        try {
            env.execute("flink mysql demo");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
