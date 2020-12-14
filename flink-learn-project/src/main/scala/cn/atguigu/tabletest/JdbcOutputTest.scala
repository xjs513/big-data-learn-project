package cn.atguigu.tabletest

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.scala._
import org.apache.flink.table.descriptors.{Csv, FileSystem, Schema}

/*
 * @Author songzhanliang
 * @Description //TODO $end$
 * @Date $time$ $date$
 * @Param $param$
 * @return $return$
 */
object JdbcOutputTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // 创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)

    // 读取文件, 注册表
//    val filePath = "D:\\IdeaProjects\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt"
//    tableEnv
//      .connect(new FileSystem().path(filePath))
//      .withFormat(new Csv())
//      .withSchema(
//        new Schema()
//          .field("id", DataTypes.STRING())
//          .field("ts", DataTypes.BIGINT())
//          .field("trt", DataTypes.DOUBLE())
//      )
//      .createTemporaryTable("inputTable")
//      val sensorTable: Table = tableEnv.from("inputTable")
    // 读取 socket 数据
    val socketStream = env.socketTextStream("kasa", 4444)
    val inputStream = socketStream.map(line => {
      val arr = line.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })



    val sensorTable: Table = tableEnv.fromDataStream(inputStream)

    // 聚合转换
    val aggTable = sensorTable
      .groupBy($"id")    // 分组
      .select($"id", $"id".count() as "cnt")

    val sinkDDL =
      """
        |create table jdbcOutputTable(
        | id varchar(20) not null,
        | cnt bigint not null
        |) with (
        | 'connector.type' = 'jdbc',
        | 'connector.url' = 'jdbc:mysql://kasa:3306/test',
        | 'connector.table' = 'sensor_count',
        | 'connector.driver' = 'com.mysql.jdbc.Driver',
        | 'connector.username' = 'root',
        | 'connector.password' = '123456'
        |)
      """.stripMargin

    tableEnv.sqlUpdate(sinkDDL)

    aggTable.executeInsert("jdbcOutputTable")

    // 不需要再启动执行环境
    // tableEnv.execute("JdbcOutputTest")

  }
}
