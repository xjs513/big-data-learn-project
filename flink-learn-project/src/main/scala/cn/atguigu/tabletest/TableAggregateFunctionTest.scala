package cn.atguigu.tabletest

import java.time.Duration

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.functions.TableAggregateFunction
import org.apache.flink.types.Row
import org.apache.flink.util.Collector

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-14 17:02
*
* @description: 
*/

object TableAggregateFunctionTest {
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
    val Top2Temp = new Top2Temp

    val resultTable = sensorTable
      .groupBy($"id")
      .flatAggregate(Top2Temp($"temperature") as ("temp", "rank"))
      .select($"id", $"temp", $"rank")

    // 或者可以这样写
    //    val resultTable = sensorTable
    //      .groupBy($"id")
    //      .select($"id", avgTemp($"temperature") as "avgTemp")
    //
        resultTable.toRetractStream[Row].print("result")

    // SQL   表聚合函数没有实现目前
//    tableEnv.createTemporaryView("sensor", sensorTable)
//    tableEnv.registerFunction("avgTemp", avgTemp)
//    val resultSQLTable = tableEnv.sqlQuery(
//      """
//        |select id, avgTemp(temperature) as avgTemp
//        |from
//        | sensor
//        |group by
//        | id
//      """.stripMargin)
//
//    resultSQLTable.toRetractStream[Row].print("sql")


    env.execute("AggregateFunctionTest")
  }
}

// 定义一个类, 专门表示表聚合函数的中间状态
class Top2TempAcc {
  var hTemp:Double = Double.MinValue
  var shTemp:Double = Double.MinValue
}

// 自定义表聚合函数, 提取温度最高的两个传感器, 输出 (temp, rank)
class Top2Temp extends TableAggregateFunction[(Double,Int), Top2TempAcc] {
  override def createAccumulator():Top2TempAcc = {
    new Top2TempAcc()
  }

  // 根据新来元素更新中间聚合结果
  def accumulate(acc:Top2TempAcc, temp:Double): Unit = {
    // 判断当前温度对否大于中间聚合结果里的温度
    if (temp > acc.hTemp){
      // 如果大于最高温, 则当前占第一, 第一排到第二
      acc.shTemp = acc.hTemp
      acc.hTemp = temp
    } else if (temp > acc.shTemp){
      // 如果在最高和第二高之间, 替换第二即可
      acc.shTemp = temp
    }
  }

  // 输出结果方法
  def emitValue(acc:Top2TempAcc, out:Collector[(Double, Int)]): Unit ={
    // 这里可以优化, 先判断是否变化, 然后决定是否输出, 以减少数据流通量
    out.collect((acc.hTemp, 1))
    out.collect((acc.shTemp, 2))
  }

}
