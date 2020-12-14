package cn.atguigu.apitest

import org.apache.flink.streaming.api.scala._


object SourceTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
//    // 1. 从集合中读取数据
//    val dataList = List(
//      SensorReading("sensor_1", 1547718199, 35.8),
//      SensorReading("sensor_6", 1547718201, 15.4),
//      SensorReading("sensor_7", 1547718202, 6.7),
//      SensorReading("sensor_10", 1547718205, 38.1)
//    )
//
//    val stream1 = env.fromCollection(dataList)
//
//    stream1.print()
//
    // 2. 从文件读取数据
    val stream2 = env.readTextFile("D:\\IdeaProjects\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt")

    stream2.print()

    // 3. 从 Kafka 读取数据
//    val properties = new Properties
//    properties.setProperty("bootstrap.servers", "localhost:9092")
//    properties.setProperty("group.id", "test1")
//    val consumer = new FlinkKafkaConsumer[String](
//      "test", new SimpleStringSchema(), properties)
//    consumer.setStartFromEarliest()
//
//    val stream3 = env.addSource(consumer)
//
//    stream3.print()

    // 执行
    env.execute("source test")
  }
}
