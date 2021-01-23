package pureeffect.indexedstate

sealed trait BoxState extends Product with Serializable

object BoxState {
  final case object Open extends BoxState
  final case object Closed extends BoxState

  val open = IndexedState.modify[Closed.type, Open.type](_ => Open)
  val close = IndexedState.modify[Open.type, Closed.type](_ => Closed)
}

object IndexedStateApp {
  def main(args: Array[String]): Unit = {
    val program = for {
      _ <- BoxState.open
      - <- BoxState.close
      - <- BoxState.open
      state <- IndexedState.get
    } yield state == BoxState.Open

    println(program.run(BoxState.Closed))
  }
}
