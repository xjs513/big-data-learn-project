package com.atguigu.wc

import org.apache.flink.streaming.api.scala._

/*
 * @Author: "songzhanliang"
 * @Date: 2020/10/12 21:52
 * @Description: 流处理单词计数程序
 */
object StreamWordCount {

  val path = "E:\\IdeaProjects\\big-data-learn-project\\data\\input\\words.txt";

  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val lines: DataStream[String] = env.readTextFile(path)

    //val lines: DataStream[String] = env.socketTextStream("localhost", 9999)

    val result = lines.flatMap(_.split(" ")).map((_, 1)).keyBy(0).sum(1)


    result.print()

    env.execute()
  }
}
