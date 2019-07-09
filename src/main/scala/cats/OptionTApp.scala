package cats

import cats.implicits._
import cats.data.OptionT

object OptionTApp {
  def main(args: Array[String]): Unit = {
    val t = OptionT.pure[Either[String, ?]](5)
    val t2 = OptionT.liftF[Either[String, ?], Int](5.asRight)
    val t3 = OptionT.fromOption[Either[String, ?]](Some(5))

    val value = t3.map(_ * 10)
    println(value)
  }
}
