package com.aishu.learn

/*
  * @author : Kasa 
  * @date : 2020/11/10 11:04  
  * @descripthon :
  */
object MatchTest_1 {
  def main(args: Array[String]): Unit = {
    val inputType = 20
    inputType match {
      case 0 => println("input type is 0.")
      case 1 => println("input type is 1.")
      case 2 => println("input type is 2.")
      case _ => println("input type is other.")
    }
  }
}
