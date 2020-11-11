package com.aishu.learn.collections

/*
  * @author : Kasa 
  * @date : 2020/11/10 14:39  
  * @descripthon :
  */
object ArrayDemo {
  def main(args: Array[String]): Unit = {
//    val nums = new Array[Int](10)
//    println(nums.mkString(","))

    val nums = Array(0, 1, 2, 3)
    println(nums.mkString(","))

    nums.foreach(println)
  }
}
