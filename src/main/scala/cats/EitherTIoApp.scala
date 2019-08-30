package cats

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

import scala.io.StdIn
import scala.language.higherKinds

object EitherTIoApp extends IOApp {
  val getIntLn: EitherT[IO, String, Int] = EitherT(IO(StdIn.readLine().toInt).attempt).leftMap(_.getMessage)
  def putStrLn(s: String): EitherT[IO, Nothing, Unit] = EitherT.liftF(IO(println(s)))

  val value1: EitherT[IO, Nothing, Int] = EitherT.pure[IO, Nothing](5)
  val value2: EitherT[IO, String, Int] = implicitly[Bifunctor[EitherT[IO, ?, ?]]].leftWiden[Nothing, Int, String](value1)

  import cats.implicits._
  import cats.effect.IO
  import cats.data.EitherT

  trait Error
  trait Boom extends Error

  implicit class BifunctorOpsExt[F[_, _]: Bifunctor, A, B](val value: F[Nothing, B]) {
    def leftWiden[AA]: F[AA, B] = Bifunctor[F].leftWiden(value)
  }

  {
    val value1: EitherT[IO, Boom, Int] = EitherT.pure[IO, Boom](5)
    value1.leftWiden[Error]
  }

  {
    val value1: EitherT[IO, Nothing, Int] = EitherT.pure[IO, Nothing](5)
    value1.leftWiden[Error] // Fixed by BifunctorOpsExt
  }

  {
    val value1: EitherT[Option, Boom, Int] = EitherT.pure[Option, Boom](5)
    value1.leftWiden[Error]
  }

  {
    val value1: EitherT[Option, Nothing, Int] = EitherT.pure[Option, Nothing](5)
    value1.leftWiden[Error] //  BifunctorOpsExt
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val program = for {
      n <- getIntLn
      _ <-putStrLn(s"n=$n").leftWiden[String]
    } yield ()

    program.fold[ExitCode](_ => ExitCode.Error, _ => ExitCode.Success)
  }
}
