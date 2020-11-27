package com.atguigu.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_WordCount {
  def main(args: Array[String]): Unit = {
    // TODO 建立和Spark框架的连接
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark01_WordCount")
    val sc: SparkContext = new SparkContext(conf)
    // TODO 读取数据 逐行读取
    val fileRdd: RDD[String] = sc.textFile("data/input/spark_01/a.txt")
    // TODO 处理、转换数据
    val wordRDD: RDD[String] = fileRdd.flatMap(_.split(" "))


    val groupRDD: RDD[(String, Iterable[String])] = wordRDD.groupBy(a => a)

    // val wordCountRDD: RDD[(String, Int)] = groupRDD.mapValues(_.size)
    // OR use next map method to finish
    val wordCountRDD: RDD[(String, Int)] = groupRDD.map[(String, Int)]{
      case (word, iterable) => {
        (word, iterable.size)
      }
    }

     val tuples: Array[(String, Int)] = wordCountRDD.collect()
     println(tuples.mkString(", "))
//    wordCountRDD.foreach(println) // ShuffleBlockFetcherIterator
    // TODO 关闭连接
    sc.stop()

  }
}
