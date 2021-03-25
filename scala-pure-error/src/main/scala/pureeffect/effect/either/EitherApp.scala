package pureeffect.effect.either

sealed trait Error extends Product with Serializable

object Error {
  final case class InvalidInteger(s: String) extends Error
  final case object DivisionByZero extends Error
}

object EitherApp {
  import Error._

  def parseInt(s: String): Either[InvalidInteger, Int] =
    if (s.matches("-?[0-9]+")) Either.succeed(s.toInt) else Either.fail(InvalidInteger(s))

  def inverse(i: Double): Either[DivisionByZero.type, Double] = {
    if (i != 0) Either.succeed(1.0 / i) else Either.fail(DivisionByZero)
  }

  def program(input: String): Either[Error, (Int, Double)] = for {
    number <- parseInt(input)
    inverseNumber <- inverse(number)
  } yield (number, inverseNumber)

  def main(args: Array[String]): Unit = {
    val attemptedResult: Either[Error, (Int, Double)] = program("0")
    println(attemptedResult)
  }
}
