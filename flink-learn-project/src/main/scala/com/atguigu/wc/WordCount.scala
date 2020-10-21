package com.atguigu.wc

<<<<<<< HEAD:flink-learn-project/src/main/scala/batch/WordCountBatch.scala
import org.apache.flink.api.scala.{AggregateDataSet, DataSet, ExecutionEnvironment}

import org.apache.flink.api.scala._

object WordCountBatch {
=======
import org.apache.flink.api.scala._
>>>>>>> 09c976b8b8d54f165e2c80338b1e0268cf5436bb:flink-learn-project/src/main/scala/com/atguigu/wc/WordCount.scala

/*
 * @Author: "songzhanliang"
 * @Date: 2020/10/12 21:43
 * @Description: 
 */
object WordCount {
  val path = "E:\\IdeaProjects\\big-data-learn-project\\data\\input\\words.txt";

  def main(args: Array[String]): Unit = {
    val environment = ExecutionEnvironment.getExecutionEnvironment
    val lines = environment.readTextFile(path)
    val words: DataSet[String] = lines.flatMap(line => line.split(" "))
    val wordOne = words.map((_, 1))
    val result: DataSet[(String, Int)] = wordOne.groupBy(0).sum(1)
    result.print()
  }
}
