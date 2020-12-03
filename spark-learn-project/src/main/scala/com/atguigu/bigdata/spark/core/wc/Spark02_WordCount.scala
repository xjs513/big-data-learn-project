package com.atguigu.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, SparkConf, SparkContext}

object Spark02_WordCount {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark01_WordCount")
    val sc: SparkContext = new SparkContext(conf)

    val fileRdd: RDD[String] = sc.textFile("spark-learn-project/data/input/spark_01/a.txt")

    val wordRDD: RDD[String] = fileRdd.flatMap(_.split(" "))

    val pairRDD: RDD[(String, Int)] = wordRDD.map((_, 1))

    val partitions: Array[Partition] = pairRDD.partitions

//    val wordGroupRdd: RDD[(String, Iterable[(String, Int)])] = pairRDD.groupBy(tuples => tuples._1)
//    val wordCountRDD: RDD[(String, Int)] = wordGroupRdd.map {
//      case (_, list) => list.reduce((t1, t2) => (t1._1, t1._2 + t2._2))
//    }
    // 上面4句可以用下面依据实现
    val wordCountRDD: RDD[(String, Int)] = pairRDD.reduceByKey(_ + _)

    val tuples: Array[(String, Int)] = wordCountRDD.collect()

    println(tuples.mkString(", "))

    sc.stop()

  }
}
