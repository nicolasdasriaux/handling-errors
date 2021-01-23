package pureerror.validation

import zio.prelude.Validation
import pureerror.eitherext.EitherExtension._
import zio.{App, ExitCode, URIO, ZIO, console}

case class Point(x: Int, y: Int)

case class Rectangle(p1: Point, p2: Point)

object Field {
  def parse(field: Option[String]): Either[String, String] = field.toRight("undefined")
}

object IntField {
  def parse(field: String): Either[String, Int] =
    Either.apply(field.toInt).refineToOrDie[NumberFormatException].mapError(_ => s"invalid integer format ($field)")

  def parse(field: Option[String]): Either[String, Int] = Field.parse(field).flatMap(parse)
}

case class PointForm(x: Option[String], y: Option[String])

object PointForm {
  def parse(form: PointForm): Validation[String, Point] =
    Validation.mapParN(
      Validation.fromEither(IntField.parse(form.x)).mapError(e => s"x: $e"),
      Validation.fromEither(IntField.parse(form.y)).mapError(e => s"y: $e"),
    )((x, y) => Point(x, y))
}

case class RectangleForm(p1: PointForm, p2: PointForm)

object RectangleForm {
  def parse(form: RectangleForm): Validation[String, Rectangle] =
    Validation.mapParN(
      PointForm.parse(form.p1).mapError(e => s"p1.$e"),
      PointForm.parse(form.p2).mapError(e => s"p2.$e")
    )((p1, p2) => Rectangle(p1, p2))
}

object ValidationApp {
  def parseInt2(s: String): Either[String, Int] = Either.apply(s.toInt).mapError(_ => s"Invalid integer format ($s)")

  def parsePoint2(x: String, y: String): Validation[String, Point] =
    Validation.mapParN(
      Validation.fromEither(parseInt2(x)).mapError(e => s"x: $e"),
      Validation.fromEither(parseInt2(y)).mapError(e => s"y: $e")
    )((x, y) => Point(x, y))

  def parseRectangle2(x1: String, y1: String, x2: String, y2: String): Validation[String, Rectangle] =
    Validation.mapParN(
      parsePoint2(x1, y1).mapError(e => s"p1.$e"),
      parsePoint2(x2, y2).mapError(e => s"p2.$e")
    )((p1, p2) => Rectangle(p1, p2))

  def main(args: Array[String]): Unit = {
    val success: Validation[Nothing, Int] = Validation.succeed(1)
    val failure: Validation[String, Nothing] = Validation.fail("Error")

    val bothSuccess: Validation[Nothing, (Int, Int)] = Validation.tupledPar(
      Validation.succeed(1),
      Validation.succeed(2)
    )

    println(bothSuccess)

    val firstFailure: Validation[String, (Nothing, Int)] = Validation.tupledPar(
      Validation.fail("X"),
      Validation.succeed(2)
    )

    println(firstFailure)

    val secondFailure: Validation[String, (Int, Nothing)] = Validation.tupledPar(
      Validation.succeed(1),
      Validation.fail("Y")
    )

    println(secondFailure)

    val bothFailure: Validation[String, (Nothing, Nothing)] = Validation.tupledPar(
      Validation.fail("X"),
      Validation.fail("Y")
    )

    println(bothFailure)

    def parseInt(s: String): Validation[String, Int] = Validation.apply(s.toInt).mapError(_ => s"Invalid integer format ($s)")

    def parsePoint(x: String, y: String): Validation[String, Point] =
      Validation.mapParN(
        parseInt(x).mapError(e => s"x: $e"),
        parseInt(y).mapError(e => s"y: $e")
      )((x, y) => Point(x, y))

    println(parsePoint("a", "b"))

    println(parseRectangle2("1", "2", "3", "4"))
    println(parseRectangle2("aaaa", "2", "3", "dddd"))
  }
}

object FullApp extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val program =
      for {
        x1 <- console.putStr("x1 > ") *> console.getStrLn
        x2 <- console.putStr("x2 > ") *> console.getStrLn
        y2 <- console.putStr("y2 > ") *> console.getStrLn
        form = RectangleForm(PointForm(Some(x1), Some(y2)), PointForm(Some(x2), Some(y2)))
        result <- ZIO.fromEither(RectangleForm.parse(form).toEither).either
        _ <- result match {
          case Right(rectangle) => console.putStrLn(rectangle.toString)
          case Left(errors) => ZIO.foreach(errors) { error =>
            console.putStrLn(error)
          }
        }
      } yield ()

    program.exitCode
  }
}
