package pureeffect.either

sealed trait Error extends Product with Serializable
case object DivisionByZero extends Error
case class InvalidInteger(s: String) extends Error

object EitherApp {
  def main(args: Array[String]): Unit = {
    val success: Either[Nothing, Int] = Either.succeed(1)
    val failure: Either[String, Nothing] = Either.fail("Error")

    def parseInt(s: String): Either[InvalidInteger, Int] =
      if (s.matches("-?[0-9]+")) Either.succeed(s.toInt) else Either.fail(InvalidInteger(s))

    def inverse(i: Double): Either[DivisionByZero.type, Double] = {
      if (i != 0) Either.succeed(1.0 / i) else Either.fail(DivisionByZero)
    }

    val sum: Either[Error, (Int, Double)] = for {
      number <- parseInt("0")
      d <- inverse(number)
    } yield (number, d)

    print(sum)
  }
}
