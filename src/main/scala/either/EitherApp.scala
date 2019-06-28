package either

sealed trait Error extends Product with Serializable
case object DivisionByZero extends Error
case class InvalidIntegerString(s: String) extends Error

object EitherApp {
  def main(args: Array[String]): Unit = {
    val left: Either[String, Int] = Either.left("Error")
    val right: Either[String, Int] = Either.right(1)

    def parseInt(s: String): Either[InvalidIntegerString , Int] =
      if (s.matches("-?[0-9]+")) Either.right(s.toInt) else Either.left(InvalidIntegerString(s))

    def inverse(i: Double): Either[DivisionByZero.type, Double] =
      Either.cond(i != 0, 1.0 / i, DivisionByZero)

    val sum = for {
      number <- parseInt("0")
      d <- inverse(number)
    } yield (number, d)

    println(sum)
  }
}
