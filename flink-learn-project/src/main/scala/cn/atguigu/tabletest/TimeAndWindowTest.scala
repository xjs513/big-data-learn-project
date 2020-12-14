package cn.atguigu.tabletest

import java.time.Duration

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.types.Row

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-11 11:39
*
* @description: 
*/

object TimeAndWindowTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // 创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)

//    val inputStream = env.readTextFile("D:\\IdeaProjects\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt")
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

    // 处理时间
//    val sensorTable = tableEnv.fromDataStream(dataStream,$"id", $"timestamp", $"temperature", $"pt".proctime)
//    sensorTable.printSchema()
//    sensorTable.toAppendStream[Row].print()

    // 事件时间
//    val sensorTable = tableEnv.fromDataStream(dataStream,$"id", $"timestamp".rowtime, $"temperature")
//    val sensorTable = tableEnv.fromDataStream(dataStream,$"id", $"temperature", $"timestamp".rowtime)
    val sensorTable = tableEnv.fromDataStream(dataStream,
      $"id", $"timestamp" as "ts", $"temperature", $"rt".rowtime)

    // Group Window table api
//    val resultTable = sensorTable
//      // .window(Tumble.over(10.seconds).on($"rt").as($"w"))
//      .window(Tumble over 10.seconds on $"rt" as $"w")
//      .groupBy($"w", $"id")
//      .select($"id", $"id".count(), $"temperature".avg(), $"w".start(), $"w".end())
//
//    // Group Window SQL
    tableEnv.createTemporaryView("sensor", sensorTable)
//
//    val resultSQLTalbe = tableEnv.sqlQuery(
//      """
//        |select
//        | id,
//        | count(id),
//        | avg(temperature),
//        | TUMBLE_START(rt, INTERVAL '10' second) AS wStarata,
//        | TUMBLE_END(rt, INTERVAL '10' second)  AS wEnd
//        |from
//        | sensor
//        |group by
//        | id,
//        | TUMBLE(rt, INTERVAL '10' second)
//      """.stripMargin)

    // 转换成流打印输出
    //    resultTable.toAppendStream[Row].print("resultTable")
    //    resultSQLTalbe.toAppendStream[Row].print("resultSQLTalbe")

    // Over window table api
    // todo: 统计每个传感器每条数据与之前的两条记录的平均温度
//    val overResultTable = sensorTable
//        .window(Over partitionBy $"id" orderBy $"rt" preceding 2.rows as $"w")
//        .select($"id", $"timestamp", $"id".count() over $"w", $"temperature".avg() over $"w")


    // SQL
//    val overResultTable = tableEnv.sqlQuery(
//      """
//        |select
//        | id,
//        | ts,
//        | temperature,
//        | count(id) over (partition by id order by rt rows between 2 PRECEDING AND CURRENT ROW),
//        | avg(temperature) over (partition by id order by rt rows between 2 PRECEDING AND CURRENT ROW)
//        |from
//        | sensor
//      """.stripMargin)

    val overResultTable = tableEnv.sqlQuery(
      """
        |select
        | id,
        | ts,
        | temperature,
        | count(id) over w,
        | avg(temperature) over w
        |from
        | sensor
        |window w as (partition by id order by rt rows between 2 PRECEDING AND CURRENT ROW)
      """.stripMargin)

    overResultTable.toAppendStream[Row].print("resultTable")

    env.execute("TimeAndWindowTest")

  }
}