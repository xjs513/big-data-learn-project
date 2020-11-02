package com.atguigu.sinktest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.functions.RichFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : Kasa
 * @date : 2020/11/2 9:52
 * @descripthon : 以 MySQL 的 jdbc 为例, 自定义 SinkFunction
drop table if exists sensor_temp;
create table if not exists sensor_temp (
    id varchar(20) not null,
    temp double not null
);
 */
public class JdbcSinkTest {
    public static void main(String[] args) {
        // 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        // 2. 从文件读取数据
        String inputPath = "data/flink/sensor.txt";
        DataStreamSource<String> inputStream = env.readTextFile(inputPath);

        // 3. 做类型转换
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] split = line.split(",");
            return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
        });



        // 4. 输出数据  http://localhost:9202/sensor/_search?pretty
        dataStream.addSink(new MyJdbcSinkFunc());

        // 5. 执行
        try {
            env.execute("JdbcSinkTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// 实际业务要考虑容错, 一致性, 重启等因素
class MyJdbcSinkFunc extends RichSinkFunction<SensorReading> {

    // 定义连接对象和写入命令
    private Connection conn;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;

    @Override
    public void open(Configuration parameters) throws Exception {
        conn = DriverManager.getConnection(
            "jdbc:mysql://dev201:3306/test?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false",
            "root",
            "Mosh123456"
        );
        insertStmt = conn.prepareStatement("insert into sensor_temp(id, temp) values (?, ?)");
        updateStmt = conn.prepareStatement("update sensor_temp set temp = ? where id = ?");

    }

    @Override
    public void invoke(SensorReading value, Context context) {
        // 先执行更新操作, 查到就更新
        try {
            updateStmt.setDouble(1, value.getTemperature());
            updateStmt.setString(2, value.getId());
            updateStmt.execute();
            if (updateStmt.getUpdateCount() == 0){
                insertStmt.setString(1, value.getId());
                insertStmt.setDouble(2, value.getTemperature());
                insertStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}
