package pureeffect.io

import scala.io.StdIn


object console {
  val getStrLn: IO[String] = IO.effectTotal(StdIn.readLine())
  def putStrLn(s: String): IO[Unit] = IO.effectTotal(println(s))
}

object IOApp {
  def main(args: Array[String]): Unit = {
    val program = for {
      _ <- console.putStrLn("What's your name?")
      name <- console.getStrLn
      _ <- console.putStrLn(s"Hello $name!")
    } yield ()

    program.unsafeRun()
  }
}
