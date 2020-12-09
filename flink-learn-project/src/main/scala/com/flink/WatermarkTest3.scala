package com.flink

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.common.eventtime._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.{ProcessWindowFunction, WindowFunction}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/*
  * @author : Kasa 
  * @date : 2020/12/8 16:17  
  * @descripthon :
  *
  * https://www.cnblogs.com/qiu-hua/p/13429952.html
  *
  * https://blog.csdn.net/Jerseywwwwei/article/details/108028528
  *
  */

object WatermarkTest3 {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    env.setParallelism(1)
    env.getConfig.setAutoWatermarkInterval(1000L)

    val socketStream = env.socketTextStream("localhost", 4444)

    val dataStream = socketStream.map(str => {
      val strings = str.split(",")
      SensorReading(strings(0), strings(1).toLong, strings(2).toDouble)
    })
    val ds = dataStream.assignTimestampsAndWatermarks(
      new WatermarkStrategy[SensorReading] {
        override def createWatermarkGenerator(context: WatermarkGeneratorSupplier.Context) = {
          new WatermarkGenerator[SensorReading]{
            var maxTimesStamp = Long.MinValue + 3000L
            // 每来一条数据，将这条数据与maxTimesStamp比较，看是否需要更新watermark
            override def onEvent(event: SensorReading, eventTimestamp: Long, output: WatermarkOutput): Unit = {
              maxTimesStamp = maxTimesStamp.max(event.timestamp)
              println("extract timestamp!!!:  " + maxTimesStamp)
            }
            // 周期性更新watermark
            override def onPeriodicEmit(output: WatermarkOutput): Unit = {
              // 允许乱序数据的最大限度为3s// 允许乱序数据的最大限度为3s
              val maxOutOfOrderness = 3000L
              // println("generate watermark!!!" + (maxTimesStamp - maxOutOfOrderness) + "ms")
              output.emitWatermark(new Watermark(maxTimesStamp - maxOutOfOrderness))
            }
          }
        }
      }.withTimestampAssigner(
        new SerializableTimestampAssigner[SensorReading] {
          // 告诉系统第二个字段是时间戳，时间戳的单位是毫秒
          override def extractTimestamp(element: SensorReading, recordTimestamp: Long): Long = {
            element.timestamp
          }
        }
      )
    )
        .keyBy(_.id)
        .timeWindow(Time.seconds(5))
      .process(new ProcessWindowFunction[SensorReading,Object,String,TimeWindow] {
        override def process(key: String, context: Context, elements: Iterable[SensorReading], out: Collector[Object]): Unit = {
          context.currentWatermark
        }

      })


//        .apply(new WindowFunction[SensorReading,Object,String,TimeWindow] {
//          override def apply(key: String, window: TimeWindow, input: Iterable[SensorReading], out: Collector[Object]): Unit = {
//            println("window : [" + window.getStart + ", " + window.getEnd + "]")
//            input.foreach(s => out.collect(s))
//          }
//        })


    ds.print()

    env.execute()

  }
}

