package cats

import cats.data.State

object StateApp {
  def main(args: Array[String]): Unit = {
    val initIndex: State[Int, Unit] = State.set[Int](1)
    val currentName: State[Int, String] = State.inspect[Int, String](i => s"Name $i")
    val currentIndex: State[Int, Int] = State.get[Int]
    val nextIndex: State[Int, Unit] = State.modify[Int](_ + 1)
    val newName: State[Int, String] = State[Int, String](i => (i + 1, s"Name $i"))

    val program: State[Int, (String, String, String, Int)] = for {
      _ <- initIndex
      name1 <- currentName
      _ <- nextIndex
      name2 <- newName
      name3 <- newName
      index <- currentIndex
    } yield (name1, name2, name3, index)

    val result: (String, String, String, Int) = program.runA(0).value
    println(result)
  }
}
