package pureeffect.effect.state

object StateApp {
  val incId: State[Int, Unit] = State.modify[Int](_ + 1)
  val currentId: State[Int, Int] = State.get[Int]

  val nextName: State[Int, String] =
    for {
      id <- currentId
      _ <- incId
    } yield s"Name $id"

  val program: State[Int, List[String]] =
    for {
      name1 <- nextName
      name2 <- nextName
      name3 <- nextName
    } yield List(name1, name2, name3)

  def main(args: Array[String]): Unit = {
    val stateAndNames: (Int, List[String]) = program.run(1)
    println(stateAndNames)
  }
}
