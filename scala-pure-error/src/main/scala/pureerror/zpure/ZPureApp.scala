package pureerror.zpure

import zio.prelude.fx.ZPure

object ZPureApp {
  def main(args: Array[String]): Unit = {
    val program = for {
      _ <- ZPure.set(1)
      n1 <- ZPure.modify[Int, Option[Int], Int](n => (Some(n + 1), n))

      n2 <- ZPure.modify[Option[Int], Option[Int], Int] {
        case None => (None, -1)
        case Some(n) => (Option(n + 1), n)
      }

      _ <- ZPure.get
    } yield n2

    println(program.run(()))
  }
}
