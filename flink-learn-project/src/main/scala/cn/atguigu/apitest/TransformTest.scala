package cn.atguigu.apitest

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.streaming.api.scala._

/*
 * @Author songzhanliang
 * @Description //TODO $end$
 * @Date $time$ $date$
 * @Param $param$
 * @return $return$
 */
object TransformTest {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 2. 从文件读取数据
    val inputStream = env.readTextFile("D:\\IdeaProjects\\big-data-learn-project\\flink-learn-project\\src\\main\\resources\\sensor.txt")

    // 转换成样例类
    val dataStream = inputStream.map(data =>{
      val arr = data.split(",")
      SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
    })

    // 分组聚合,输出每个传感器当前最低温度
    // val aggStream = dataStream.keyBy(_.id).minBy("temperature")

    // 输出当前最低温度以及最大时间戳
    val rsultStream = dataStream.keyBy(_.id).reduce(
      (s1, s2) => {
        SensorReading(s1.id, s2.timestamp,s1.temperature.min(s2.temperature))
      }
    )

    rsultStream.print()

    env.execute()
  }
}
