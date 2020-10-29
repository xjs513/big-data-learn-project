package com.atguigu.apitest;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author : Kasa
 * @date : 2020/10/29 14:45
 * @descripthon :
 */
public class StateTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置默认并行度
        env.setParallelism(1);

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        String host = parameterTool.get("host");
        int port = parameterTool.getInt("host");

        DataStream<String> elements = env.socketTextStream("dev201", 13579);

        DataStream<Tuple2<String, Integer>> tuples = elements.flatMap(
                (FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
                    String[] strings = s.split(" ");
                    for (String string : strings) {
                        collector.collect(new Tuple2<>(string, 1));
                    }
                }
        ).setParallelism(3).returns(Types.TUPLE(Types.STRING, Types.INT));

        DataStream<Tuple2<String, Integer>> result = tuples
                .keyBy((KeySelector<Tuple2<String, Integer>, String>) tuple2 -> tuple2.f0)
                .sum(1).setParallelism(3);

        try {
            result.print();
            env.execute("StreamingWordCountSocket");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Keyed state 必须定义在 RichFunction中, 因为需要运行时上下文

/**
 * getRuntimeContext() 不能直接调用, 要在类实例化之后, 即open 生命周期中
 */

class MyRichMapper extends RichMapFunction<String, String>{

    private ValueState<Double> valueState;

    private ListState<Double> listState;

    private MapState<String, Double> mapState;

    private ReducingState<Tuple2<String, Integer>> reducingState;

    @Override
    public void open(Configuration parameters) throws Exception {
        valueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("valueState", Types.DOUBLE)
//        new ValueStateDescriptor<>("valueState", Double.TYPE)
//        new ValueStateDescriptor<Double>("valueState", Double.class)
        );

        listState = getRuntimeContext().getListState(
                new ListStateDescriptor<>("listState", Types.DOUBLE)
        );

        mapState = getRuntimeContext().getMapState(
                new MapStateDescriptor<>("mapState", Types.STRING, Types.DOUBLE)
        );

        reducingState = getRuntimeContext().getReducingState(
                new ReducingStateDescriptor<>(
                        "reducingState",
                        (ReduceFunction<Tuple2<String, Integer>>) (t1, t2) -> new Tuple2<>(t1.f0, t1.f1 + t2.f1)
                        , Types.TUPLE(Types.STRING, Types.INT)
                )
        );
    }



//    @Override
//    public RuntimeContext getRuntimeContext() {
//        return super.getRuntimeContext();
//    }

    @Override
    public String map(String s) throws Exception {

        // 读取状态
        Double value = valueState.value();
        // 对状态赋值
        valueState.update(0D);

        //
        listState.add(0D);
        listState.addAll(new ArrayList<Double>() {});
        listState.update(new ArrayList<Double>() {});
        Iterable<Double> doubles = listState.get();
        //
        Iterable<Map.Entry<String, Double>> entries = mapState.entries();

        //
        Tuple2<String, Integer> tempTuple2 = reducingState.get();

        reducingState.add(new Tuple2<>("name", 10));


        return null;
    }
}