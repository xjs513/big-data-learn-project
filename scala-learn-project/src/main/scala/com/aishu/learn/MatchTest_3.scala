package com.aishu.learn

/*
  * @author : Kasa 
  * @date : 2020/11/10 11:11  
  * @descripthon :
  */
object MatchTest_3 {
  def main(args: Array[String]): Unit = {
//    var person:Any = null
//    person = ("mike")
//    person = ("tom", "teacher")
//    person = ("rose", "student", "123456")
//
//    person = 90
//
//    person match {
//      case per:(String) => println("name " + per)
//      case teacher:(String, String) => println("name " + teacher._1 + " ocupation " + teacher._2)
//      case stu:(String, String, Int) => println("name " + stu._1 + " ocupation " + stu._2 + " id " + stu._3)
//      case _ => println("other type")
//    }

    val input: Int = 1
    val rs = input match {
      case 0 => "num 0"
      case 1 => "num 1"
      case _ => "other value"
    }
    println("rs " + rs)

  }
}
