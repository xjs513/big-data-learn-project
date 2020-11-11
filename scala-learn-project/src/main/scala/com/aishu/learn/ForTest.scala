package com.aishu.learn

/*
  * @author : Kasa 
  * @date : 2020/11/10 11:18  
  * @descripthon :
  */
object ForTest {
  def main(args: Array[String]): Unit = {
//    for (i <- 0 until 5) {
//      println("i = " + i)
//    }
    // for 返回值 可以用 yield 收集for循环中产生的值
    // val rs = for (i <- 0 until 5) yield i * 2
    // println(rs.mkString("[", ",", "]"))
    // for + 守卫
//    val ar = Array(0, 1, 2, 2, 5, 4, 8, 9)
//    val rs = for (i <- ar if (i%2) == 0) yield i
//    println(rs.mkString("[", ",", "]"))
    // for + 多个守卫
    val ar = Array(0, 1, 2, 2, 5, 4, 8, 9)
    val rs = for (i <- ar if (i%2) ==0 && i > 2) yield i
    println(rs.mkString("[", ",", "]"))
  }
}
