package com.flink

import com.flink.source.SensorSource
import org.apache.flink.streaming.api.scala._

/*
  * @author : Kasa 
  * @date : 2020/12/9 10:02  
  * @descripthon :
  */
object SourceTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val inputStream = env.addSource(new SensorSource)

    inputStream.print()

    env.execute()
  }
}
