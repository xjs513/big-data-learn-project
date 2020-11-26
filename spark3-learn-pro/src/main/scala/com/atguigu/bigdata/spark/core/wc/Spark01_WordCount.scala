package com.atguigu.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
 * @author : Kasa
 * @date : 2020/11/19 14:53
 * @descripthon :
 */
object Spark01_WordCount {
   def main(args: Array[String]): Unit = {
     // 参数准备
     val conf = new SparkConf()
       .setMaster("local[*]")
       .setAppName("Spark01_WordCount")
     // 环境入口
     val sc = new SparkContext(conf)

     // 计算和存储操作
     val lines: RDD[String] = sc.textFile("spark3-learn-pro/data/input/data")
     val tuples: RDD[(String, Int)] = lines.flatMap(_.split(" ")).map((_, 1))

     val values: RDD[(String, Int)] = tuples.reduceByKey(_ + _)

     val arr: Array[(String, Int)] = values.collect()
     for(t <- arr) {
       println(t._1 + "=" + t._2)
     }
     // 关闭连接和环境
     sc.stop()
   }
}
