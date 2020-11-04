//package com.atguigu.apitest
//
//import org.apache.flink.api.common.functions.FlatMapFunction
//import org.apache.flink.streaming.api.scala._
//import org.apache.flink.util.Collector
//
//
//
///*
// * @Author: "songzhanliang"
// * @Date: 2020/11/3 21:26
// * @Description:
// */
//object StateTestScala {
//  def main(args: Array[String]): Unit = {
//    val env = StreamExecutionEnvironment.getExecutionEnvironment
//
//    env.setParallelism(1)
//
//    val lines: DataStream[String] = env.socketTextStream("localhost", 7777)
//
////    lines.flatMap(new FlatMapFunction[String, SensorReading] {
////      override def flatMap(t: String, collector: Collector[SensorReading]) = {
////        val strings = t.split(", ")
////        collector.collect(SensorReading(strings(0), strings(1).toLong, strings(2).toDouble))
////      }
////    })
//
//    val dataStream = lines.map(line => {
//      val strings = line.split(",")
//      SensorReading(strings(0), strings(1).toLong, strings(2).toDouble)
//    })
//    val result = dataStream.keyBy(_.id)
//      .flatMapWithState[(String, Double, Double), Double] {
//        case (data: SensorReading, None) => (List.empty, Some(data.temperature))
//        case (data: SensorReading, lastTemp: Some[Double]) => {
//          val diff = (data.temperature - lastTemp.get).abs
//          if (diff > 10.0D)
//            (List((data.id, data.timestamp, data.temperature)), Some(data.temperature))
//          else
//            (List.empty, Some(data.temperature))
//        }
//      }
//
//    result.print()
//
//    env.execute("StateTest")
//  }
//}
//
//case class SensorReading(id:String, timestamp:Long, temperature:Double)