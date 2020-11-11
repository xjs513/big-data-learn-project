package com.aishu.learn.functions

/*
  * @author : Kasa 
  * @date : 2020/11/10 16:09  
  * @descripthon :
  */
object FunctionDemo_2 {

  def test():Unit = {
    val getNum1 = (n:Int) => {
      if (n < 3) {
        println("n < 3")
        -1
      } else {
        println("n >= 3")
        n + 1
      }
    }

    def getNum2(n:Int):Int = {
      if (n < 3) {
        println("n < 3")
        return -1
      } else {
        println("n >= 3")
        return n + 1
      }
      0
    }

    val rs = (1 to 10).map(getNum1)
    rs.foreach(n => print(n + " "))

  }

  def main(args: Array[String]): Unit = {
    test()
  }
}
