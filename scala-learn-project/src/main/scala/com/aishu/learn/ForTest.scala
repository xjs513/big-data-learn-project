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
//    val ar = Array(0, 1, 2, 2, 5, 4, 8, 9)
//    val rs = for (i <- ar if (i%2) ==0 && i > 2) yield i
//    println(rs.mkString("[", ",", "]"))

    //声明第一个函数
    def func(): Int = {
      println("computing stuff....")
      42 // return something
    }
    //声明第二个函数，scala默认的求值就是call-by-value
    def callByValue(x: Int): Unit = {
      println("1st x: " + x)
      println("2nd x: " + x)
    }
    //声明第三个函数，用=>表示call-by-name求值
    def callByName(x: => Int): Unit = {
      println("1st x: " + x)
      println("2nd x: " + x)
    }
    //开始调用

    //call-by-value求值
    callByValue(func())
    //输出结果
    //computing stuff....
    //1st x: 42
    //2nd x: 42

    //call-by-name求值
    callByName(func())
    //输出结果
    //computing stuff....
    //1st x: 42
    //computing stuff....
    //2nd x: 42
  }
}
