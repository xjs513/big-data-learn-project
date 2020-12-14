package cn.atguigu.tabletest

import java.time.Duration

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.functions.ScalarFunction
import org.apache.flink.types.Row

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-14 15:30
*
* @description: 
*/

object ScalarFunctionTest {
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
    val hashCode = new HashCode(23)

    val resultTable = sensorTable
        .select($"id", $"ts", hashCode($"id"))

    // SQL
    tableEnv.createTemporaryView("sensor", sensorTable)
    tableEnv.registerFunction("hashCode", hashCode)
    val resultSQLTable = tableEnv.sqlQuery(
      """
        |select id, ts, hashCode(id) from sensor
      """.stripMargin)


    resultTable.toAppendStream[Row].print("result")
    resultSQLTable.toAppendStream[Row].print("sql")


    env.execute("ScalarFunctionTest")
  }
}

// 自定义标量函数
class HashCode(factor:Int) extends ScalarFunction{
  def eval(s:String):Int = {
    s.hashCode * factor - 10000
  }
}
