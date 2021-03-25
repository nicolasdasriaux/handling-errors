package pureeffect.transformer.statet

import cats.data.StateT

object StateTApp {
  type Position = Int
  def main(args: Array[String]): Unit = {
    import Move._

    val moveForward: Int => Option[(Position, Move)] = { position => if (position + 1 <= 2) Some((position + 1, Forward)) else None }
    val state: StateT[Option, Position, Move] = StateT.pure[Option, Position, Move](Forward)
    val init: StateT[Option, Int, Unit] = StateT.set[Option, Int](0)
  }
}
