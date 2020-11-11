package com.aishu.learn.collections

import scala.collection.mutable.ArrayBuffer

/*
  * @author : Kasa 
  * @date : 2020/11/10 14:54  
  * @descripthon :
  */
object ArrayBufferDemo {
  def main(args: Array[String]): Unit = {
    val arrBuf = new ArrayBuffer[Int]()
    arrBuf.append(1, 2, 3)

    println(arrBuf.mkString(" | "))
  }
}
