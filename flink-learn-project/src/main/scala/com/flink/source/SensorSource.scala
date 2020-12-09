package com.flink.source

import java.util.concurrent.TimeUnit

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}

import scala.util.Random

/*
  * @author : Kasa 
  * @date : 2020/12/9 9:52  
  * @descripthon :
  */
class SensorSource() extends RichSourceFunction[SensorReading]{

  var running = true
  override def open(parameters: Configuration): Unit = {
//    index = 0
  }

  override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
    while (running){
      ctx.collectWithTimestamp(
        SensorReading("sensor_" + Random.nextInt(10), System.currentTimeMillis(), 100 * Random.nextDouble()),
        System.currentTimeMillis()
      )
      TimeUnit.MILLISECONDS.sleep(500L)
    }
  }

  override def cancel(): Unit = {
    running = false
  }
}
