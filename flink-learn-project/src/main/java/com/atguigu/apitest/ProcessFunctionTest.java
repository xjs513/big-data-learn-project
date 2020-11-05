package com.atguigu.apitest;

import com.atguigu.modules.SensorReading;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/*
 * @Author: songzhanliang
 * @Date: 2020/11/4 00:13
 * @Description:
 */
public class ProcessFunctionTest {
    public static void main(String[] args) {
        // 1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从调用时刻开始给 env 创建的每一个 stream 追加时间特征
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.setParallelism(1);

        // 2. 从文件读取数据
        // String inputPath = "data/flink/sensor.txt";
        // DataStreamSource<String> inputStream = env.readTextFile(inputPath);
        DataStream<String> inputStream = env.socketTextStream("dev201", 13579);

        // 3. 做类型转换
        DataStream<String> dataStream = inputStream
                .map(line -> {
                    String[] split = line.split(",");
                    return new com.atguigu.modules.SensorReading(split[0], Long.parseLong(split[1]), Double.parseDouble(split[2]));
                })
                .keyBy(SensorReading::getId)
                // .process(new MyKeyedProcessFunction());
                .process(new TempIncWarning(10 * 1000));

        // 4. 输出或保存结果数据
        dataStream.print();

        // 5. 执行
        try {
            env.execute("JdbcSinkTest");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

// TODO: 连续 10 秒温度上升则输出报警 窗口函数和session都不满足
// 实现自定义的 KeyedProcessFunction
class TempIncWarning extends KeyedProcessFunction<String, SensorReading, String>{

    // 定义状态: 保存上一个温度值进行比较, 保存定时器的时间戳, 用于删除定时器
    private ValueState<Double> lastTempState;
    private ValueState<Long> timerTsState;

    private long interval;

    private static final double initTemp = -273.15;
    private static final long initTimerTs =  0;

    public TempIncWarning(long interval){
        this.interval = interval;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        lastTempState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("lastTempState", Types.DOUBLE)
        );

        timerTsState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("timerTsState", Types.LONG)
        );
    }

    @Override
    public void processElement(SensorReading value, Context ctx, Collector<String> out) throws Exception {
        // 每条数据都调用该方法
        // 1. 先取出状态
        Double lastTemp = lastTempState.value() == null ? initTemp : lastTempState.value();
        Long timerTs = timerTsState.value() == null ? initTimerTs : timerTsState.value();

        long ts = ctx.timerService().currentProcessingTime() + interval;
        if (timerTs == 0){
            // 进来第一个数据, 只更新状态, 不注册定时器
            System.out.println("第一个数据, 只更新状态, 不注册定时器");
            timerTsState.update(ts);
            lastTempState.update(lastTemp);
        } else if (value.getTemperature() > lastTemp){
            // 如果温度上，那么注册当前数据时间戳10秒之后的定时器
            ctx.timerService().registerProcessingTimeTimer(ts);
            System.out.println("注册定时器: " + ts);
            // 保存状态
            timerTsState.update(ts);
            lastTempState.update(lastTemp);
        } else if (value.getTemperature()  < lastTemp) {
            // 如果温度下降, 则删除定时器
            ctx.timerService().deleteProcessingTimeTimer(timerTs);
            timerTsState.clear();
            System.out.println("清除定时器: " + timerTs);
        }
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
        System.out.println("触发定时器: " + timestamp);
        out.collect("传感器[" + ctx.getCurrentKey() + "]的温度连续[" + interval/1000 + "]秒上升, 上次温度是:" + lastTempState.value());
        timerTsState.clear();
    }
}


// class TempChangeAlert extends RichFlatMapFunction



class MyKeyedProcessFunction extends KeyedProcessFunction<String, SensorReading, String>{

    @Override
    public void processElement(SensorReading sensorReading, Context context, Collector<String> collector) throws Exception {
        context.getCurrentKey(); // 获取当前的 key
        context.timestamp();     // 时间戳

        // context.output();     // 测输出流

        context.timerService().currentWatermark();
        // 注册定时器, 参数是定时器触发的事件 触发时调用 onTimer
        // 多个定时器就是通过触发时间来区分
        context.timerService().registerEventTimeTimer(1000L);

        // 删除某个定时器
        context.timerService().deleteEventTimeTimer(1000L);
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
