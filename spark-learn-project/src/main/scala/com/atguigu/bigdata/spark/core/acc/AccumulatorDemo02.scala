package com.atguigu.bigdata.spark.core.acc

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

object AccumulatorDemo02 {
  def main(args: Array[String]): Unit = {
    // todo : Spark 创建运行环境
    val conf: SparkConf = new SparkConf()
      .setAppName("AccumulatorDemo02")
      .setMaster("local[*]")
    val sc: SparkContext = new SparkContext(conf)

    // 注册累加器
    val sum: LongAccumulator = sc.longAccumulator("sum")

    //TODO 3.创建一个RDD new ParallelCollectionRDD
    val rdd: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4))

    // RDD 中操作累加器
    rdd.foreach{
      case i:Int => {
        sum.add(i)
      }
    }

    // 取回累加器的值
    println("sum = " + sum.value)

    sc.stop()
  }
}
