package com.atguigu.wc

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time


/*
  * @author : Kasa 
  * @date : 2020/11/2 18:33  
  * @descripthon :
  */
object WindowAndWaterMarkTest {
  def main(args: Array[String]): Unit = {
    // 1. 创建执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // 从调用时刻开始给 env 创建的每一个 stream 追加时间特征
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val inputStream: DataStream[String] = env.socketTextStream("dev201", 13579)

    // 3. 做类型转换
    val dataStream: DataStream[(String, Long, Double)] = inputStream.map(line => {
      val split: Array[String] = line.split(",")
      (split(0), split(1).toLong, split(2).toDouble)
    })

    val lateOutputTag = new OutputTag[(String, Long, Double)]("late")

    val result: DataStream[(String, Long, Double)] = dataStream
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[(String, Long, Double)](Time.seconds(3)) {
        override def extractTimestamp(element: (String, Long, Double)): Long = element._2
      })
      .keyBy((_: (String, Long, Double))._1)
      .timeWindow(Time.seconds(15))
      .allowedLateness(Time.minutes(1))
      .sideOutputLateData(lateOutputTag)
      .reduce(
        (t1, t2) => (t1._1, t2._2, t1._3.min(t2._3))
      )

    val late: DataStream[(String, Long, Double)] = result.getSideOutput(lateOutputTag)

    late.print("late")
    result.print("result")

    // 5. 执行
    env.execute("JdbcSinkTest")
  }
}
