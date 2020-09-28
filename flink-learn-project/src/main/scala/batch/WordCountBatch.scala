package batch

import org.apache.flink.api.scala.{AggregateDataSet, DataSet, ExecutionEnvironment}

import org.apache.flink.api.sca
la._

object WordCountBatch {

  val path = "E:\\IdeaProjects\\big-data-learn-project\\data\\input\\words.txt";

  def main(args: Array[String]): Unit = {
    val environment = ExecutionEnvironment.getExecutionEnvironment
    val lines = environment.readTextFile(path)
    val words: DataSet[String] = lines.flatMap(line => line.split(" "))
    val wordOne = words.map((_, 1))
    val result: DataSet[(String, Int)] = wordOne.groupBy(0).sum(1)
    result.print()
  }
}
