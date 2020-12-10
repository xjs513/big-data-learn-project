package cn.atguigu.tabletest

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.descriptors.{Csv, Kafka, Schema}

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-10 17:14
*
* @description: 
*/

object KafkPipelineTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // 创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)

  tableEnv
    .connect(
      new Kafka()
        .version("universal")
        .topic("sensor")
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

    val sensorTable: Table = tableEnv.from("kafkaInputTable")

    // 简单转换
    val resultTable = sensorTable
      .select($"id", $"temperature")
      .filter($"id" === "sensor_1")

    // 聚合转换
    val aggTable = sensorTable
      .groupBy($"id")    // 分组
      .select($"id", $"id".count() as "count")

    // 输出到 Kafka  只支持 Append 模式
    tableEnv
      .connect(
        new Kafka()
          .version("universal")
          .topic("sinktest")
          .property("zookeeper.connect", "dev201:2181")
          .property("bootstrap.servers", "dev201:9092")
      )
      .withFormat(new Csv())
      .withSchema(
        new Schema()
          .field("id", DataTypes.STRING())
          .field("temperature", DataTypes.DOUBLE())
      )
      .createTemporaryTable("kafkaOutputTable")

    resultTable.insertInto("kafkaOutputTable")

    tableEnv.execute("KafkPipelineTest")
  }
}

/**
kafka-console-producer --broker-list dev201:9092 --topic sensor

kafka-console-consumer --bootstrap-server dev201:9092 --topic sensor
kafka-console-consumer --bootstrap-server dev201:9092 --topic sinktest
  */