package pureeffect.transformer

import cats._
import cats.data._
import cats.implicits._
import cats.effect._

import scala.io.StdIn

object ReaderEitherIOApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val t: ReaderT[EitherT[IO, Int, *], Int, String] =
      ReaderT[EitherT[IO, Int, *], Int, String] { (i: Int) =>
        EitherT[IO, Int, String] {
          IO(Right(i.toString))
        }
      }

    t.run(1).value.as(ExitCode.Success)
  }
}
