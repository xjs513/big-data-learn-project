package com.atguigu.apitest;

import com.atguigu.modules.SensorReading;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

        // 检查点配置参数检查点配置参数
/*        env.enableCheckpointing(1000L);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(60000L);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500L);
        env.getCheckpointConfig().setPreferCheckpointForRecovery(true);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);*/

        // 重启策略配置
/*        env.setRestartStrategy(RestartStrategies.failureRateRestart(5, Time.of(2, TimeUnit.SECONDS), Time.seconds(2)));
        env.setRestartStrategy(RestartStrategies.failureRateRestart(5, Time.seconds(2), Time.seconds(2)));


        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(5)));
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 1000L));*/
        //2.默认的重启策略是：固定延迟无限重启
        //此处设置重启策略为：出现异常重启3次，隔5秒一次
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(5)));

//        ParameterTool parameterTool = ParameterTool.fromArgs(args);
//
//        String host = parameterTool.get("host");
//        int port = parameterTool.getInt("host");

        DataStream<String> elements = env.socketTextStream("dev201", 13579);

        DataStream<SensorReading> dataStream = elements.flatMap(
                (FlatMapFunction<String, SensorReading>) (s, collector) -> {
                    String[] strings = s.split(",");
                    SensorReading sr = new SensorReading();
                    sr.setId(strings[0]);
                    sr.setTimestamp(Long.valueOf(strings[1]));
                    sr.setTemperature(Double.valueOf(strings[2]));
                    collector.collect(sr);
                }
        ).returns(SensorReading.class);

        // TODO: 传感器温度值跳变超过 10 度, 触发报警
        SingleOutputStreamOperator<Tuple3<String, Double, Double>> alertStream = dataStream
                .keyBy(SensorReading::getId)
                .flatMap(new TempChangeAlert(10.0D));

        alertStream.print();

        try {
            env.execute("StateTest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class TempChangeAlert extends RichFlatMapFunction<SensorReading,Tuple3<String, Double, Double>> {

    TempChangeAlert(double threshold){
        this.threshold = threshold;
    }

    private double threshold;

    // 定义状态,保存上次温度值
    private ValueState<Double> lastTemp;

    @Override
    public void open(Configuration parameters) throws Exception {


//        ValueStateDescriptor<Double> descriptor =
//                new ValueStateDescriptor<>(
//                        "lastTemp", // the state name
//                        TypeInformation.of(new TypeHint<Double>() {}), // type information
//                        Double.MIN_VALUE); // default value of the state, if nothing was set
//        lastTemp = getRuntimeContext().getState(descriptor);

        lastTemp = getRuntimeContext().getState(
                new ValueStateDescriptor<>("lastTemp", Types.DOUBLE)
        );
        // lastTemp.update(0D);
    }

    @Override
    public void flatMap(SensorReading sensorReading, Collector<Tuple3<String, Double, Double>> collector) throws Exception {
        double newTemp = sensorReading.getTemperature();
        if (lastTemp.value() == null){
            // 更新状态
            this.lastTemp.update(newTemp);
            return;
        }
        double stateTemp = lastTemp.value();
        System.out.println("stateTemp = " + stateTemp);
        System.out.println("sensorReading = " + sensorReading);
        // 更新状态
        this.lastTemp.update(newTemp);
        if (Math.abs(stateTemp - newTemp) > threshold)
            collector.collect(new Tuple3<>(sensorReading.getId(), stateTemp, newTemp));
    }
}

//

/**
 * Keyed state 必须定义在 RichFunction中, 因为需要运行时上下文
 * getRuntimeContext() 不能直接调用, 要在类实例化之后, 即 open 函数中
 * scala 可以用 lazy 关键字来实现, 懒加载, 实际用到的时候上下文肯定已经存在
 * name 绝对唯一
 */

class MyRichMapper extends RichMapFunction<SensorReading, String>{

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

    @Override
    public String map(SensorReading s) throws Exception {

        // 读取状态
        Double value = valueState.value();
        // 对状态赋值
        valueState.update(0D);

        // 追加一个元素
        listState.add(0D);
        // 追加一组元素
        listState.addAll(new ArrayList<Double>(){});
        // 更新一组元素, 替换原有元素
        listState.update(new ArrayList<Double>() {});
        // 获取当前的值 获取后遍历
        Iterable<Double> doubles = listState.get();


        // 判断是否包含
        mapState.contains("key");
        // 根据 key 获取 value
        mapState.get("key");
        // 添加或更新元素
        mapState.put("key", 1.0D);
        // 追加一组元素
        mapState.putAll(new HashMap<String, Double>(){{
            put("1", 1.1D);
            put("2", 2.2D);
        }});
        // 获取当前的值 获取后遍历
        Iterable<Map.Entry<String, Double>> entries = mapState.entries();

        // 读取状态
        Tuple2<String, Integer> tempTuple2 = reducingState.get();
        // 聚合新元素
        reducingState.add(new Tuple2<>("name", 10));

        return null;
    }
}