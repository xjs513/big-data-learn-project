package scala.learn.aishu

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/10 20:37
 * @Description: 
 */
object ConvertDemo {

  implicit def array2String1(ar:Array[Int])= {
    ar.mkString("[", ",", "]")
  }

  def printStr(str:String):Unit = {
    println(str)
  }

  def main(args: Array[String]): Unit = {
    val arr = Array(1, 2, 3)
    printStr(arr)

    "192.168.0.18".getIpArray().foreach(println)
  }

  implicit class Array2String(ip:String){
    def getIpArray() = ip.split('.').map(_.toInt)
  }

}
