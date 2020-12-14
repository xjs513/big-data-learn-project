package cn.atguigu.tabletest

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.descriptors._

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-10 10:29
*
* @description: 
*/

object TableApiTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)

    /*
    // 1.1 基于 old planner 的流处理
    val settings = EnvironmentSettings.newInstance()
      .useOldPlanner()
      .inStreamingMode()
      .build()

    val oldStreamTableEnv = StreamTableEnvironment.create(env, settings)

    // 1.2 基于老版本的 批处理
    val batchEnv = ExecutionEnvironment.getExecutionEnvironment
    val oldBatchTableEnv = BatchTableEnvironment.create(batchEnv)


    // 1.3 基于 Blink planner 的流处理
    val blinkStreamSettings = EnvironmentSettings.newInstance()
      .useBlinkPlanner()
      .inStreamingMode()
      .build()

    val blinkStreamTableEnv = StreamTableEnvironment.create(env, blinkStreamSettings)

    // 1.4 基于 Blink planner 的批处理
    val blinkBatchSettings = EnvironmentSettings.newInstance()
      .useBlinkPlanner()
      .inBatchMode()
      .build()

    val blinkBatchTableEnv = TableEnvironment.create(blinkBatchSettings)
    */

    // 2. 连接外部系统, 读取数据, 注册表
    // 2.1 读取文件
    val filePath = "C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt"

    tableEnv
      .connect(new FileSystem().path(filePath))
      .withFormat(new OldCsv())
      .withSchema(
        new Schema()
          .field("id", DataTypes.STRING())
          .field("ts", DataTypes.BIGINT())
          .field("trt", DataTypes.DOUBLE())
      )
      .createTemporaryTable("inputTable")
    val inputTable: Table = tableEnv.from("inputTable")
//    inputTable.toAppendStream[SensorReading].print()
//    inputTable.toAppendStream[(String, Long, Double)].print()

    // 2.2 从 kafka 读取数据

/*    tableEnv
      .connect(
        new Kafka()
          .version("universal")
          .topic("test")
          .property("zookeeper.connect", "dev201:2181")
          .property("bootstrap.servers", "dev201:9092")
          .startFromEarliest()
      )
      .withFormat(new Csv())
      .withSchema(
        new Schema()
          .field("id", DataTypes.STRING())
          .field("timestamp", DataTypes.BIGINT())
          .field("temperature", DataTypes.DOUBLE())
      )
      .createTemporaryTable("kafkaInputTable")

    val inputTable: Table = tableEnv.from("kafkaInputTable")
    inputTable.toAppendStream[(String, Long, Double)].print() */

    // 3. 查询转换
    // 3.1 使用 Table API
    val resultTable = inputTable
        .select($"ts", $"id",$"trt")
        .filter($"id" === "sensor_1")

    // 3.2 SQL
/*    val resultSqlTable = tableEnv.sqlQuery(
      """
        |select
        | id,
        | temperature
        |from
        | inputTable
        |where
        | id = 'sensor_1'
      """.stripMargin)*/

    resultTable.toAppendStream[(Long, String, Double)].print("resultTable")
//    resultSqlTable.toAppendStream[(String, Double)].print("resultSqlTable")
    env.execute()



  }
}