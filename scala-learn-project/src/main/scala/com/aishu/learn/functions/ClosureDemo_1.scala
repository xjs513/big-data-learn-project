package com.aishu.learn.functions

/*
  * @author : Kasa 
  * @date : 2020/11/10 16:33  
  * @descripthon :
  */
object ClosureDemo_1 {
  def main(args: Array[String]): Unit = {
    val funArray: Array[Int => Int] = new Array[Int => Int](4)

//    var i = 0
//    while (i < funArray.length){
//      val temp = i
//      funArray(i) = (num:Int) => num * temp
//      i += 1
//    }

    for (i <- funArray.indices) {
      funArray(i) = (num:Int) => num * i
    }

    funArray.foreach(f => println(f(2)))

  }
}
