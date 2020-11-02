package com.atguigu.sinktest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : Kasa
 * @date : 2020/11/2 9:52
 * @descripthon :
 */
public class EsSinkTest {
    public static void main(String[] args) {
        // 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 从文件读取数据
        String inputPath = "data/flink/sensor.txt";
        DataStreamSource<String> inputStream = env.readTextFile(inputPath);

        // 3. 做类型转换
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] split = line.split(",");
            return new SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
        });


        // 定义 HttpHosts
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("localhost", 9202));



        ElasticsearchSinkFunction<SensorReading> myEsSinkFunc = new ElasticsearchSinkFunction<SensorReading>() {
            @Override
            public void process(SensorReading t, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                // 包装数据
                HashMap<String, String> dataSource = new HashMap<>();
                dataSource.put("id", t.getId());
                dataSource.put("temperature", String.valueOf(t.getTemperature()));
                dataSource.put("ts", String.valueOf(t.getTimestamp()));
                // 创建 index request, 用于发送创建索引的请求
                IndexRequest indexRequest = Requests.indexRequest()
                        .index("sensor")
                        .type("_doc")
                        .source(dataSource);
                // 发送请求
                requestIndexer.add(indexRequest);
            }
        };

        ElasticsearchSink.Builder<SensorReading> esSinkBuilder = new ElasticsearchSink.Builder<>(httpHosts, myEsSinkFunc);

        // 4. 输出数据  http://localhost:9202/sensor/_search?pretty
        dataStream.addSink(esSinkBuilder.build());

        // 5. 执行
        try {
            env.execute("EsSinkTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

