package com.atguigu.bigdata.spark.core.rdd

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object Spark01_RDD_Memory {
  def main(args: Array[String]): Unit = {
    // todo : Spark 创建运行环境
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark01_RDD_Memory")
    val sc: SparkContext = new SparkContext(conf)
    // TODO : Spark 从内存中创建 RDD
    // todo : 1.parallelize 并行
    // todo : 2.makeRDD 实际也是调用了 parallelize
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5))

    println(rdd.partitions.length)

    // spark 3.0.0 运行报错,2.4.4 和 2.4.5 可以
    rdd.saveAsTextFile("spark-learn-project\\data\\output\\Spark01_RDD_Memory")

    // spark-learn-project\data\input\spark_01\*.txt

//    rdd.collect().foreach(println)

    sc.stop()
  }
}
