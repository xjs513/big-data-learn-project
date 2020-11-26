package com.atguigu.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
 * @author : Kasa
 * @date : 2020/11/19 14:53
 * @descripthon :
 */
object Spark02_WordCount {
   def main(args: Array[String]): Unit = {
     // 参数准备
     val conf = new SparkConf()
       .setMaster("local[*]")
       .setAppName("Spark02_WordCount")
     // 环境入口
     val sc = new SparkContext(conf)


     // 计算和存储操作
     val lines: RDD[String] = sc.textFile("spark3-learn-pro/data/input/data")

     val words: RDD[String] = lines.flatMap(_.split(" "))

     val wordGroup: RDD[(String, Iterable[String])] = words.groupBy(w => w)

     val word2count: RDD[(String, Int)] = wordGroup.map {
       case (word, list) => (word, list.size)
     }


     val arr: Array[(String, Int)] = word2count.collect()
     for(t <- arr) {
       println(t._1 + "=" + t._2)
     }
     // 关闭连接和环境
     sc.stop()
   }
}
