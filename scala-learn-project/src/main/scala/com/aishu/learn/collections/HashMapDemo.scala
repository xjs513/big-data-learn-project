package com.aishu.learn.collections

import scala.collection.immutable.HashMap

/*
  * @author : Kasa 
  * @date : 2020/11/10 14:46  
  * @descripthon :
  */
object HashMapDemo {
  def main(args: Array[String]): Unit = {
    val stuInfo = HashMap(
      (1001, "Tom"),
      (1002, "Mike"),
      (1003, "Rose"),
      (1004, "Tom")
    )

    println(stuInfo.getOrElse(1002, "NoName"))
    println(stuInfo.getOrElse(1004, "NoName"))

    for (elem <- stuInfo) {
      println("id:" + elem._1 + ", name:" + elem._2)
    }

  }
}
