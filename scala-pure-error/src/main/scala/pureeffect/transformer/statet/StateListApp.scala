package pureeffect.transformer.statet

import cats.data.StateT

object StateListApp {
  type Position = Int

  def main(args: Array[String]): Unit = {
    import Move._

    val possibleMoves: Position => List[(Position, Move)] = { position =>
      val maybeForwardMove = if (position + 1 <= 2) Some((position + 1, Forward)) else None
      val maybeBackwardMove = if (position - 1 >= -2) Some((position - 1, Backward)) else None
      val sleepMove = (position, Sleep)

      maybeForwardMove.toList ++ maybeBackwardMove.toList :+ sleepMove
    }

    val possibleMovesT: StateT[List, Position, Move] = StateT[List, Position, Move](possibleMoves)

    val value: StateT[List, Int, List[Move]] = for {
      m1 <- possibleMovesT
      m2 <- possibleMovesT
      m3 <- possibleMovesT
    } yield List(m1, m2, m3)

    println(value.run(1))
  }
}
