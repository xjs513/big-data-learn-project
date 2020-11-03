package com.atguigu.wc

import org.apache.flink.streaming.api.scala._

/*
  * @author : Kasa 
  * @date : 2020/11/3 9:53  
  * @descripthon :
  */
object Test {
  def main(args: Array[String]): Unit = {
    println("Hello World.")
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment


    val lines: DataStream[Int] = env.fromElements(1, 2, 3)

    lines.print()

    env.execute()
  }
}
