package cn.atguigu.tabletest

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{Table, TableColumn}
import org.apache.flink.table.api.bridge.scala._

/*
* @project: flink-learn-project
*
* @author: songzhanliang
*
* @create: 2020-12-10 09:55
*
* @description: 
*/

object Example {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 2. 从文件读取数据
//    val inputStream = env.readTextFile("D:\\IdeaProjects\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt")
    val inputStream = env.readTextFile("C:\\kasa_work_pros\\kasa_learn_pro\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt")
    val dataaStream = inputStream.map(
      data => {
        val arr = data.split(",")
        SensorReading(arr(0),arr(1).toLong,arr(2).toDouble)
      }
    )

    // 首先创建表执行环境
    val tableEnv = StreamTableEnvironment.create(env)

    // 基于流创建表
    val dataTable:Table = tableEnv.fromDataStream(dataaStream)

    val resultTable: Table = dataTable.select("id, temperature").filter("id = 'sensor_1'")


    // 直接用 SQL 实现
    tableEnv.createTemporaryView("dataTable", dataTable)
    val sql = "select id, temperature from dataTable where id = 'sensor_1'"

    val sqlResultTable: Table = tableEnv.sqlQuery(sql)

    val explaination:String = tableEnv.explain(resultTable)
    println(explaination)

    resultTable.toAppendStream[(String, Double)].print("resultTable")
    sqlResultTable.toAppendStream[(String, Double)].print("sqlResultTable")

    //
    // sqlResultTable.executeInsert("")

    env.execute("tabble api example")

  }
}