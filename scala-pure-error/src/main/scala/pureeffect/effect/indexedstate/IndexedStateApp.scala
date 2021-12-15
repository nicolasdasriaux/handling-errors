package pureeffect.effect.indexedstate

sealed trait Lock extends Product with Serializable

object Lock {
  final case object Open extends Lock
  final case object Closed extends Lock
}

case class Safe[L <: Lock] private(lock: L, openingCount: Int)

object Safe {
  import Lock._

  def initial: Safe[Closed.type] = Safe(lock = Closed, openingCount = 0)

  val open: IndexedState[Safe[Closed.type], Safe[Open.type], Unit] =
    IndexedState.modify { safe =>
      safe.copy(
        lock = Open,
        openingCount = safe.openingCount + 1
      )
    }

  val close: IndexedState[Safe[Open.type], Safe[Closed.type], Unit] =
    IndexedState.modify { safe => safe.copy(lock = Closed) }
}

object IndexedStateApp {
  import Lock._

  def main(args: Array[String]): Unit = {
    val program: IndexedState[Safe[Closed.type], Safe[Open.type], Int] = for {
      _ <- Safe.open
      - <- Safe.close
      - <- Safe.open
      safe <- IndexedState.get
    } yield safe.openingCount

    val safeAndOpeningCount: (Safe[Open.type], Int) = program.run(Safe.initial)
    println(safeAndOpeningCount)
  }
}
