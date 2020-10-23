package com.atguigu.wc

<<<<<<< HEAD
=======
<<<<<<< HEAD:flink-learn-project/src/main/scala/batch/WordCountBatch.scala
>>>>>>> 0ba8e892613cd3768f1b47f663371747d4c1b08f
import org.apache.flink.api.scala.{AggregateDataSet, DataSet, ExecutionEnvironment}

import org.apache.flink.api.scala._

<<<<<<< HEAD
=======
object WordCountBatch {
=======
import org.apache.flink.api.scala._
>>>>>>> 09c976b8b8d54f165e2c80338b1e0268cf5436bb:flink-learn-project/src/main/scala/com/atguigu/wc/WordCount.scala

>>>>>>> 0ba8e892613cd3768f1b47f663371747d4c1b08f
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
