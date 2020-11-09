package kasa.test.kafka.tomysql.demo3;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/5 21:38
 * @Description:
 */
public class DriverDemo3 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<SourceVo> source = fsEnv.addSource(new MysqlSource());
        source.addSink(new MysqlSink());
        fsEnv.execute();
    }
}
