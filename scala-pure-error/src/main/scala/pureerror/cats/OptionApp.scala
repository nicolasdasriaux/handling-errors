package pureerror.cats

import cats.implicits._

object OptionApp {
  def main(args: Array[String]): Unit = {
    val someInt = 3.some
    val noneInt = none[Int]
  }
}
