package com.flink

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark

/*
  * @author : Kasa 
  * @date : 2020/12/8 16:17  
  * @descripthon :
  */

object WatermarkTest2 {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    env.getConfig.setAutoWatermarkInterval(10000L)

    val socketStream = env.socketTextStream("localhost", 4444)

    val dataStream = socketStream.map(str => {
      val strings = str.split(",")
      SensorReading(strings(0), strings(1).toLong, strings(2).toDouble)
    })
    val ds = dataStream.assignTimestampsAndWatermarks(
      // generate periodic watermarks
      new AssignerWithPeriodicWatermarks[SensorReading] {
        val bound = 10 * 1000L // 最大延迟时间
        var maxTs = Long.MinValue + bound + 1 // 当前观察到的最大时间戳

        // 用来生成水位线
        // 默认200ms调用一次
        override def getCurrentWatermark: Watermark = {
          println("generate watermark!!!" + (maxTs - bound - 1) + "ms")
          new Watermark(maxTs - bound - 1)
        }

        // 每来一条数据都会调用一次
        override def extractTimestamp(t: SensorReading, l: Long): Long = {
          println("extract timestamp!!!")
          maxTs = maxTs.max(t.timestamp) // 更新观察到的最大事件时间
          t.timestamp // 抽取时间戳
        }
      }
    )


    ds.print()

    env.execute()

  }
}

