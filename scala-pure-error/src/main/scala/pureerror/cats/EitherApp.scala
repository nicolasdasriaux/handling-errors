package pureerror.cats

import cats.implicits._

object EitherApp {
  trait Error extends Product with Serializable
  case class InvalidIntString(s: String) extends Error
  case class NegativeInt(i: Int) extends Error

  def main(args: Array[String]): Unit = {
    val right: Right[Nothing, Int] = Right(5)
    val left: Left[String, Nothing] = Left("Error")

    val rightEither: Either[Nothing, Int] = Either.right(5)
    val leftEither: Either[String, Nothing] = Either.left("Error")

    val stringOrInt: Either[String, Int] = 5.asRight[String]
    val leftEither2: Either[String, Int] = "Error".asLeft[Int]

    def validateIntString(s: String): Either[InvalidIntString, Int] =
      if (s.matches("""-?\d+"""))
        Either.right(s.toInt)
      else
        Either.left(InvalidIntString(s))

    def validatePositiveInt(n: Int): Either[NegativeInt, Int] =
      Either.cond(
        n >= 10,
        n,
        NegativeInt(n)
      )

    val errorOrInt: Either[Error, Int] = for {
      n <- validateIntString("-5")
      _ <- validatePositiveInt(n)
    } yield n

    println(validateIntString("123"))
    println(validateIntString("123-BOOM"))

    val abc: Either[InvalidIntString, Int] = validateIntString("123").map(_ * 10)
    println(validateIntString("BOOM").leftMap({ case InvalidIntString(s) => s"Invalid int string ($s)" }))

    println(validateIntString("-4").flatMap(validatePositiveInt))

    def parseIntCond(s: String): Either[InvalidIntString, Int] =
      Either.cond(
        s.forall(_.isDigit),
        s.toInt,
        InvalidIntString(s)
      )
  }
}
