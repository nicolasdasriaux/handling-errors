package pureeffect.transformer.statet

import cats.data.StateT
import pureeffect.transformer.statet.StateListApp.Position

object StateOptionApp {
  def main(args: Array[String]): Unit = {
    import Move._

    val possibleMove: Position => Option[(Position, Move)] = { position =>
      if (position + 1 <= 2) Some((position + 1, Forward)) else None
    }

    val possibleMoveT: StateT[Option, Position, Move] = StateT[Option, Position, Move](possibleMove)

    val value: StateT[Option, Position, List[Move]] = for {
      m1 <- possibleMoveT
      m2 <- possibleMoveT
      m3 <- possibleMoveT
    } yield List(m1, m2, m3)

    val result = value.run(2)
    println(result)
  }
}
