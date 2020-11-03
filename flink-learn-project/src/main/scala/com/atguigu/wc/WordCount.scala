package com.atguigu.wc

import org.apache.flink.api.scala._

/*
 * @Author: "songzhanliang"
 * @Date: 2020/10/12 21:43
 * @Description:
 */
object WordCount {
  val path = "data\\input\\words.txt";

  def main(args: Array[String]): Unit = {
    val environment = ExecutionEnvironment.getExecutionEnvironment
    val lines = environment.readTextFile(path)
    val words: DataSet[String] = lines.flatMap(line => line.split(" "))
    val wordOne = words.map((_, 1))
    val result: DataSet[(String, Int)] = wordOne.groupBy(0).sum(1)
    result.print()
  }
}
