package com.aishu.learn.functions

/*
  * @author : Kasa 
  * @date : 2020/11/10 15:41  
  * @descripthon :
  */
object FunctionDemo {

  val add1 = (num1: Int, num2:Int) => { num1 + num2 }

  def add2= (num1: Int, num2:Int) => { num1 + num2 }

  def main1:Array[String] =>Unit = (arr:Array[String]) => {
    val a = add1(3, 4)
    println(a)
    val b = add2(90, 10)
    println(b)
    // ()
  }

  def main(args: Array[String]): Unit = {
    val value = main1(Array[String]())
    println(value)
  }

}
