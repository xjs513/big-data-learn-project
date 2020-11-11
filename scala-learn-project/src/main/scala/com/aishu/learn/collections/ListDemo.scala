package com.aishu.learn.collections

/*
  * @author : Kasa 
  * @date : 2020/11/10 14:57  
  * @descripthon :
  */
object ListDemo {
  def main(args: Array[String]): Unit = {
    val list = List[Int](12, 23)
    val list2 = list:::list++list

    println(list)
    println(list2)

  }
}
