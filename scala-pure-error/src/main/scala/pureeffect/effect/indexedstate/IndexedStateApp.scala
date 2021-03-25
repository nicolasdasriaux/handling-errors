package pureeffect.effect.indexedstate

sealed trait BoxState extends Product with Serializable

object BoxState {
  final case object Open extends BoxState
  final case object Closed extends BoxState

  val open: IndexedState[Closed.type, Open.type, Unit] = IndexedState.modify[Closed.type, Open.type](_ => Open)
  val close: IndexedState[Open.type, Closed.type, Unit] = IndexedState.modify[Open.type, Closed.type](_ => Closed)
}

object IndexedStateApp {
  import BoxState._

  def main(args: Array[String]): Unit = {
    val program: IndexedState[Closed.type, Open.type, Boolean] = for {
      _ <- BoxState.open
      - <- BoxState.close
      - <- BoxState.open
      state <- IndexedState.get
    } yield state == BoxState.Open

    println(program.run(BoxState.Closed))
  }
}
