package cn.atguigu.tabletest

import java.time.Duration

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.functions.AggregateFunction
import org.apache.flink.types.Row

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-14 16:28
*
* @description: 
*/

object AggregateFunctionTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)

    val inputStream = env.readTextFile("C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt")
    val dataStream = inputStream.map(
      data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      }
    ).assignTimestampsAndWatermarks(
      // 水位线策略；默认200ms的机器时间插入一次水位线
      // 水位线 = 当前观察到的事件所携带的最大时间戳 - 最大延迟时间
      WatermarkStrategy
        // 最大延迟时间设置为5s
        .forBoundedOutOfOrderness[SensorReading](Duration.ofSeconds(5))
        .withTimestampAssigner(new SerializableTimestampAssigner[SensorReading] {
          // 告诉系统第二个字段是时间戳，时间戳的单位是毫秒
          override def extractTimestamp(element: SensorReading, recordTimestamp: Long): Long = {
            element.timestamp * 1000L
          }
        })
    )

    val sensorTable = tableEnv.fromDataStream(dataStream,
      $"id", $"timestamp" as "ts", $"temperature", $"rt".rowtime)

    // 1. Table API
    val avgTemp = new AvgTemp

//    val resultTable = sensorTable
//      .groupBy($"id")
//      .aggregate(avgTemp($"temperature") as "avgTemp")
//        .select($"id", $"avgTemp")

    // 或者可以这样写
//    val resultTable = sensorTable
//      .groupBy($"id")
//      .select($"id", avgTemp($"temperature") as "avgTemp")
//
//    resultTable.toRetractStream[Row].print("result")

    // SQL
    tableEnv.createTemporaryView("sensor", sensorTable)
    tableEnv.registerFunction("avgTemp", avgTemp)
    val resultSQLTable = tableEnv.sqlQuery(
      """
        |select id, avgTemp(temperature) as avgTemp
        |from
        | sensor
        |group by
        | id
      """.stripMargin)

    resultSQLTable.toRetractStream[Row].print("sql")


    env.execute("AggregateFunctionTest")
  }
}


// 定义一个类, 专门表示聚合状态
class AvgTempAcc {
  var sum:Double = 0.0D
  var count:Int = 0
}

// 自定义 AggregateFunction, 求每个传感器的平均值

class AvgTemp extends AggregateFunction[Double, AvgTempAcc]{
  override def getValue(accumulator: AvgTempAcc) = {
    accumulator.sum/accumulator.count
  }

  override def createAccumulator() = {
    new AvgTempAcc
  }



  // 实现一个具体的聚合函数
  def accumulate(accumulator:AvgTempAcc, temp:Double) = {
    accumulator.sum += temp
    accumulator.count += 1
  }
}