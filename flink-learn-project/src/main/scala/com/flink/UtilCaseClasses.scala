package com.flink

/*
  * @author : Kasa 
  * @date : 2020/12/9 9:50  
  * @descripthon :
  */
object UtilCaseClasses {
  // 定义样例类 温度传感器
  case class SensorReading(id:String, timestamp:Long, temperature:Double)
}
