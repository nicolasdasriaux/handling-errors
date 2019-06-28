import cats.syntax.either._

object EitherApp {
  case class InvalidIntString(s: String)
  case class NegativeInt(i: Int)

  def main(args: Array[String]): Unit = {
    val right: Right[Nothing, Int] = Right(5)
    val left: Left[String, Nothing] = Left("Error")

    val rightEither: Either[Nothing, Int] = Either.right(5)
    val leftEither: Either[String, Nothing] = Either.left("Error")

    def parseInt(s: String): Either[InvalidIntString, Int] =
      if (s.startsWith("-") && s.drop(1).forall(_.isDigit) || s.forall(_.isDigit))
        Either.right(s.toInt)
      else
        Either.left(InvalidIntString(s))


    def positiveInt(n: Int): Either[NegativeInt, Int] =
      Either.cond(
        n >= 10,
        n,
        NegativeInt(n)
      )

    println(parseInt("123"))
    println(parseInt("123-BOOM"))

    println(parseInt("123").map(_ * 10))
    println(parseInt("BOOM").leftMap({ case InvalidIntString(s) => s"Invalid int string ($s)" }))

    println(parseInt("-4").flatMap(positiveInt))

    def parseIntCond(s: String): Either[InvalidIntString, Int] =
      Either.cond(
        s.forall(_.isDigit),
        s.toInt,
        InvalidIntString(s)
      )
  }
}
