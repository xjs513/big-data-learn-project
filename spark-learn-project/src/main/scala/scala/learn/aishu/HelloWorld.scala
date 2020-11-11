package scala.learn.aishu

import scala.collection.mutable.ArrayBuffer

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/10 00:02
 * @Description: 
 */
object HelloWorld {
  def main(args: Array[String]): Unit = {
    //val bigList = new ArrayBuffer[Int]
    //print("Hello World!Size " + bigList.size)

    var printHello:Any = 1.0
//    if (printHello)
//      println("Hello")
//    else
//      println("World")

    println(printHello.getClass)

    printHello match {
      case a:Long => println("ddd:" + a)
      case a:Double => println("xxx:" + a)
      case _    => println("ffff")
    }
  }
}
