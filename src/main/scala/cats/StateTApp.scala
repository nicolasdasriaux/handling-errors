package cats

import cats.data.StateT
import cats.implicits._

object StateTApp {
  def main(args: Array[String]): Unit = {
    val state: StateT[Option, Int, String] = StateT.pure[Option, Int, String]("Starting")
    val init: StateT[Option, Int, Unit] = StateT.set[Option, Int](0)
  }
}
