package pureerror.zio.validation

import zio.prelude._
import pureerror.eitherext.ZEither._
import zio.console.Console
import zio.{Cause => _, _}

import java.io.IOException
import scala.util.Try

object Field {
  def parse(field: Option[String]): Either[String, String] = field.toRight("undefined")
}

object IntField {
  def parse(field: String): Either[String, Int] =
    Either.attempt(field.toInt).refineToOrDie[NumberFormatException].mapError(_ => s"invalid integer format ($field)")

  def parse(field: Option[String]): Either[String, Int] = Field.parse(field).flatMap(parse)
}

case class Point(x: Int, y: Int)
case class Rectangle(p1: Point, p2: Point)

case class PointForm(x: Option[String], y: Option[String])

object PointForm {
  def parse(form: PointForm): Validation[String, Point] =
    Validation.validateWith(
      Validation.fromEither[String, Int](IntField.parse(form.x)).mapErrorAll(_.map(e => s"x: $e")),
      Validation.fromEither[String, Int](IntField.parse(form.y)).mapErrorAll(_.map(e => s"y: $e"))
    )((x, y) => Point(x, y))
}

case class RectangleForm(p1: PointForm, p2: PointForm)

object RectangleForm {
  def parse(form: RectangleForm): Validation[String, Rectangle] =
    Validation.validateWith(
      PointForm.parse(form.p1).mapErrorAll(_.map(e => s"p1.$e")),
      PointForm.parse(form.p2).mapErrorAll(_.map(e => s"p2.$e"))
    )((p1, p2) => Rectangle(p1, p2))
}

object ValidationApp {
  def main(args: Array[String]): Unit = {
    val success: Validation[Nothing, Int] = Validation.succeed(1)
    val failure: Validation[String, Nothing] = Validation.fail("Error")

    val bothSuccess: Validation[Nothing, (Int, Int)] = Validation.validate(
      Validation.succeed(1),
      Validation.succeed(2)
    )

    println(bothSuccess)

    val firstFailure: Validation[String, (Int, Int)] = Validation.validate(
      Validation.fail("X"),
      Validation.succeed(2)
    )

    println(firstFailure)

    val secondFailure: Validation[String, (Int, Int)] = Validation.validate(
      Validation.succeed(1),
      Validation.fail("Y")
    )

    println(secondFailure)

    val bothFailure: Validation[String, (Int, Int)] = Validation.validate(
      Validation.fail("X"),
      Validation.fail("Y")
    )

    println(bothFailure)

    def parseInt(s: String): Validation[String, Int] = Validation(s.toInt).mapError(_ => s"Invalid integer format ($s)")

    def parsePoint(x: String, y: String): Validation[String, Point] =
      Validation.validateWith(
        parseInt(x).mapError(e => s"x: $e"),
        parseInt(y).mapError(e => s"y: $e")
      )((x, y) => Point(x, y))

    println(parsePoint("a", "b"))
  }
}

object ConsoleExtension {
  def getStrOptionLn: ZIO[Console, IOException, Option[String]] = console.getStrLn.map(s => if (s.trim.isEmpty) None else Some(s))
}

object FullApp extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val program =
      for {
        x1 <- console.putStr("x1 > ") *> ConsoleExtension.getStrOptionLn
        y1 <- console.putStr("y1 > ") *> ConsoleExtension.getStrOptionLn
        x2 <- console.putStr("x2 > ") *> ConsoleExtension.getStrOptionLn
        y2 <- console.putStr("y2 > ") *> ConsoleExtension.getStrOptionLn
        form = RectangleForm(PointForm(x1, y1), PointForm(x2, y2))
        rectangleValidation  = RectangleForm.parse(form)

        _ <- console.putStrLn(rectangleValidation.toString)

        _ <- rectangleValidation.fold(
          success = rectangle => console.putStrLn(rectangle.toString),
          failure = errors => ZIO.foreach(errors) { error =>
            console.putStrLn(error)
          }
        )

        _ <- console.putStrLn("Test")
        _ <- rectangleValidation.toZIO.sandbox.fold(
          success = rectangle => console.putStrLn(rectangle.toString),
          failure = cause => ZIO.foreach(cause.failures) { error =>
            console.putStrLn(error)
          }
        )

        attemptedRectangle <- rectangleValidation.toZIO.sandbox.either

        _ <- console.putStrLn(attemptedRectangle.toString)

        _ <- attemptedRectangle match {
          case Right(rectangle) => console.putStrLn(rectangle.toString)

          case Left(cause) => console.putStrLn(cause.prettyPrint)  *> ZIO.foreach(cause.failures) { error =>
            console.putStrLn(error)
          }
        }
      } yield ()

    program.exitCode
  }
}
