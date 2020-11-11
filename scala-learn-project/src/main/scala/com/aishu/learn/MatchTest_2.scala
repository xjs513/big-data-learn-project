package com.aishu.learn

/*
  * @author : Kasa 
  * @date : 2020/11/10 11:06  
  * @descripthon :
  */
object MatchTest_2 {
  def main(args: Array[String]): Unit = {
    var inputType:Any = null
    inputType = 10.0
    inputType match {
      case str:String =>
        println("inputType is String")
        println(str)
      case num:Int =>
        println("inputType is Int")
        println(num)
      case _ => println("other type")
    }
  }
}
