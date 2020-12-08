package com.flink

import java.time.Duration

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkGeneratorSupplier, WatermarkStrategy}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.runtime.operators.util.AssignerWithPeriodicWatermarksAdapter

/*
  * @author : Kasa 
  * @date : 2020/12/8 16:17  
  * @descripthon :
  */

case class SensorReading(id:String, timestamp:Long, temperature:Double)

object WatermarkTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val socketStream = env.socketTextStream("localhost", 4444)

    val dataStream = socketStream.map(str => {
      val strings = str.split(",")
      SensorReading(strings(0), strings(1).toLong, strings(2).toDouble)
    })
    val ds = dataStream.assignTimestampsAndWatermarks(
      // 水位线策略；默认200ms的机器时间插入一次水位线
      // 水位线 = 当前观察到的事件所携带的最大时间戳 - 最大延迟时间
      WatermarkStrategy
        // 最大延迟时间设置为5s
        .forBoundedOutOfOrderness[SensorReading](Duration.ofSeconds(5))
        .withTimestampAssigner(new SerializableTimestampAssigner[SensorReading] {
          // 告诉系统第二个字段是时间戳，时间戳的单位是毫秒
          override def extractTimestamp(element: SensorReading, recordTimestamp: Long): Long = {
            println(element)
            println(recordTimestamp)
            recordTimestamp
          }
        })
    )


    ds.print()

    env.execute()

  }
}

