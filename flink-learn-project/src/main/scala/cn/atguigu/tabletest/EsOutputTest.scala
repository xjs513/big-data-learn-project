package cn.atguigu.tabletest

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.descriptors._

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-10 17:14
*
* @description:
*/

object EsOutputTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // 创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)

//    tableEnv
//      .connect(
//        new Kafka()
//          .version("universal")
//          .topic("sensor")
//          .property("zookeeper.connect", "dev201:2181")
//          .property("bootstrap.servers", "dev201:9092")
//          .startFromEarliest()
//      )
//      .withFormat(new Csv())
//      .withSchema(
//        new Schema()
//          .field("id", DataTypes.STRING())
//          .field("timestamp", DataTypes.BIGINT())
//          .field("temperature", DataTypes.DOUBLE())
//      )
//      .createTemporaryTable("kafkaInputTable")
//
//    val sensorTable: Table = tableEnv.from("kafkaInputTable")

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
//      .select($"id", $"temperature")
//      .filter($"id" === "sensor_1")

    // 聚合转换
    val aggTable = sensorTable
      .groupBy($"id")    // 分组
      .select($"id", $"id".count() as "cnt")

    // 输出到 ES  支持 Append | Upsert | Retract 模式
    tableEnv
      .connect(
        new Elasticsearch()
          .version("6")
          .host("dev201", 9200, "http")
          .index("sensor")
          .documentType("doc")
      )
      .inUpsertMode()
      .withFormat(new Json())
      .withSchema(
        new Schema()
          .field("id", DataTypes.STRING())
          .field("cnt", DataTypes.BIGINT())
      )
      .createTemporaryTable("esOutputTable")

    aggTable.insertInto("esOutputTable")

    tableEnv.execute("EsOutputTest")
  }
}

/**
kafka-console-producer --broker-list dev201:9092 --topic sensor

kafka-console-consumer --bootstrap-server dev201:9092 --topic sensor
kafka-console-consumer --bootstrap-server dev201:9092 --topic sinktest
  */