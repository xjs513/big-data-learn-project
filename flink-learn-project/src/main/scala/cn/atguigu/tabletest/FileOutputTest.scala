package cn.atguigu.tabletest

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.descriptors.{Csv, FileSystem, OldCsv, Schema}

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-10 15:18
*
* @description: 
*/

object FileOutputTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // 首先创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)
    // 读取文件, 注册表
    val filePath = "C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt"
    tableEnv
      .connect(new FileSystem().path(filePath))
      .withFormat(new Csv())
      .withSchema(
        new Schema()
          .field("id", DataTypes.STRING())
          .field("ts", DataTypes.BIGINT())
          .field("trt", DataTypes.DOUBLE())
      )
      .createTemporaryTable("inputTable")

    val sensorTable: Table = tableEnv.from("inputTable")

    // 简单转换
//    val resultTable = sensorTable
//      .select($"id", $"trt")
//      .filter($"id" === "sensor_1")

    // 聚合转换

    val aggTable = sensorTable
      .groupBy($"id")    // 分组
      .select($"id", $"id".count() as "count")


//    resultTable.toAppendStream[(String, Double)].print("resultTabbe")

    // 聚合输出不能用 追加流
//     aggTable.toRetractStream[(String, Long)].print("aggTable")

    // 输出到文件  只支持 Append 模式
    // 注册输出表
//    val outputPath = "C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\outputPath.txt"
    val outputPath = "C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\agg.txt"
    tableEnv
      .connect(new FileSystem().path(outputPath))
      .withFormat(new Csv())
      .withSchema(
        new Schema()
          .field("id", DataTypes.STRING())
          .field("cnt", DataTypes.BIGINT())
//          .field("trt", DataTypes.DOUBLE())
      )
      .createTemporaryTable("outputTable")

    aggTable.insertInto("outputTable")
//    resultTable.insertInto("outputTable")

    tableEnv.execute("insert into file test")
//    env.execute()
  }
}

/*
resultTabbe> (sensor_1,35.8)
resultTabbe> (sensor_1,15.4)
resultTabbe> (sensor_1,6.7)
resultTabbe> (sensor_1,38.1)
-------------------------------------
aggTable> (true,(sensor_1,1))
aggTable> (true,(sensor_6,1))
aggTable> (true,(sensor_7,1))
aggTable> (true,(sensor_10,1))
aggTable> (false,(sensor_1,1))
aggTable> (true,(sensor_1,2))
aggTable> (false,(sensor_1,2))
aggTable> (true,(sensor_1,3))
aggTable> (false,(sensor_1,3))
aggTable> (true,(sensor_1,4))
 */