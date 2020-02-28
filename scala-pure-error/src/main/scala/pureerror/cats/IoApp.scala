package pureerror.cats

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.implicits._
import cats.syntax.all._

import scala.io.StdIn

object IoApp extends IOApp {
  def putStrLn(line: String): IO[Unit] = IO {
    Console.println(line)
  }

  val getStrLn: IO[String] = IO {
    StdIn.readLine()
  }

  val program: IO[Unit] = for {
    _ <- putStrLn("What's you name?")
    name <- getStrLn
    _<- putStrLn(s"Hello $name!")
  } yield ()

  override def run(args: List[String]): IO[ExitCode] = {
    program.as(ExitCode.Success)
  }
}
