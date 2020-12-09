package com.flink.trigger

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-09 16:42
*
* @description: 
*/

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object TriggerExample {
  @throws[Exception]
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(1)
    env.socketTextStream("localhost", 9999).map(new MapFunction[String, SensorReading]() {
      @throws[Exception]
      override def map(s: String) = {
        val arr = s.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      }
    }).assignTimestampsAndWatermarks(
      WatermarkStrategy
        .forMonotonousTimestamps[SensorReading]
        .withTimestampAssigner(
          new SerializableTimestampAssigner[SensorReading]() {
            override def extractTimestamp(ele: SensorReading, l: Long) = ele.timestamp
          }
        )
    ).keyBy(_.id).timeWindow(Time.seconds(5)).trigger(new OneSecondIntervalTrigger).process(
      new ProcessWindowFunction[SensorReading, String, String, TimeWindow] {
        override def process(key: String, context: Context, elements: Iterable[SensorReading], out: Collector[String]): Unit = {
          // abc
          var count = 0L
          elements.size
          println("窗口起止["  + context.window.getStart + ", " + context.window.getEnd + "]")
          println("窗口长度["  + (context.window.getStart - context.window.getEnd) + "]")
          println("Watermark["  + context.currentWatermark + "]")
          println("窗口中有["  + elements.size + "]条记录")
        }
      }
    ).print
    env.execute
  }

}