package pureeffect.effect.list

object ListApp {
  import List._

  def main(args: Array[String]): Unit = {
    val numbers: List[Int] = for {
      i <- 1 :: 2 :: 3 :: Nil
      j <- 10 :: 100 :: 1000 :: Nil
    } yield i * j

    println(numbers)
  }
}
