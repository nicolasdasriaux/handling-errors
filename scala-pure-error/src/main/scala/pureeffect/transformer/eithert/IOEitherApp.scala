package pureeffect.transformer.eithert

import scala.language.higherKinds

import cats._
import cats.data._
import cats.implicits._
import cats.effect._

import scala.io.StdIn

object IOEitherApp extends IOApp {
  object Console {
    val getIntLn: IO[Either[Throwable, Int]] =
      IO(StdIn.readLine().toInt).attempt

    def putStrLn(s: String): IO[Either[Nothing, Unit]] =
      IO(println(s)).map(Right(_))
  }

  object ConsoleT {
    val getIntLn: EitherT[IO, Throwable, Int] = EitherT(Console.getIntLn)
    def putStrLn(s: String):  EitherT[IO, Nothing, Unit] = EitherT(Console.putStrLn(s))
  }

  implicit class BifunctorOpsExt[F[_, _]: Bifunctor, A, B](val value: F[Nothing, B]) {
    def leftWiden[AA]: F[AA, B] = Bifunctor[F].leftWiden(value)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val program = for {
      _ <- ConsoleT.putStrLn("Enter a number:")
      n <- ConsoleT.getIntLn
      _ <- ConsoleT.putStrLn(s"Number is $n.").leftWiden[Throwable]
    } yield ()

    program.fold[ExitCode](_ => ExitCode.Error, _ => ExitCode.Success)
  }
}
