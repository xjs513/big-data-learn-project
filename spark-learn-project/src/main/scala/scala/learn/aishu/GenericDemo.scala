package scala.learn.aishu

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/10 23:21
 * @Description:
 *
 *  协变（Covariance）
协变定义：对于泛型类 C [T]，如果 S 是 T 的子类（S <: T），C[S]也是 C[T]的子类（C[S] <:
C[T]），则称 C（的类型参数）是协变的。
协变的表示：在泛型前加一个+号，例如 C[+T]，就表示 C 是协变的。
List 是一个典型的协变例子，List 定义如下，其中[+A]表明 List 是协变的，也就是说，如
果类 A 是 B 的子类，那么 List[A]也是 List[B]的子类。


逆变定义：对于泛型类 C [T]，如果 S 是 T 的子类（S <: T），C[S]也是 C[T]的子类（C[S] >:
C[T]），则称 C（的类型参数）是逆变的。
逆变表示：在泛型前加一个-号，可以表示逆变，例如 C[-T]，就表示 C 是逆变的。
 *
 */
object GenericDemo {

  def sum[T](ar:Array[T]):T = {
    val rs = ar(0) match {
      case a:Int => ar.asInstanceOf[Array[Int]].sum
      case a:Char => ar.asInstanceOf[Array[Char]].mkString(" ")
      case a:String => ar.asInstanceOf[Array[String]].mkString(" ")
      case _ => null
    }
    rs.asInstanceOf[T]
  }

  def main(args: Array[String]): Unit = {
    val numList = Array(1, 3, 5, 7)
    println(sum(numList))
    val charList = Array('1', '3', '5', '7')
    println(sum(charList))
    val strList = Array("1", "3", "5", "7")
    println(sum(strList))
  }
}
