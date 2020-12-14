package cn.atguigu.tabletest

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.scala._

/*
 * @Author songzhanliang
 * @Description //TODO $end$
 * @Date $time$ $date$
 * @Param $param$
 * @return $return$
 */

object Example {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 2. 从文件读取数据
    val inputStream = env.readTextFile("D:\\IdeaProjects\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt")

    // 转换成样例类
    val dataStream = inputStream.map(data =>{
      val arr = data.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })

    // 首先创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)
    // 基于流创建一张表
    val dataTable:Table = tableEnv.fromDataStream(dataStream)

    // 调用 table api 进行转换
    val resultTable = dataTable.select("id, temperature").filter("id == 'sensor_1'")

    resultTable.toAppendStream[(String, Double)].print("result")


    // 直接 SQL 操作
    tableEnv.createTemporaryView("dataTable", dataTable)
    val sql = "select id, temperature from dataTable where id = 'sensor_1'"

    val sqlResultTable: Table = tableEnv.sqlQuery(sql)

    val explaination:String = tableEnv.explain(resultTable)
    println(explaination)

    resultTable.toAppendStream[(String, Double)].print("resultTable")
    sqlResultTable.toAppendStream[(String, Double)].print("sqlResultTable")

    env.execute("table api example")
  }
}
