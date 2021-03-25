package pureeffect.transformer.optiont

import cats.data._

object OptionTApp {
  def main(args: Array[String]): Unit = {
    val nested: Either[String, Option[Int]] = Right(Some(5))
    val apply: OptionT[Either[String, *], Int] = OptionT[Either[String, *], Int](nested)

    val value = apply.map(_ * 10)
    println(value)
    println(value.value)

    val pure: OptionT[Either[String, *], Int] = OptionT.pure[Either[String, *]](5)
    val liftF: OptionT[Either[String, *], Int] = OptionT.liftF[Either[String, *], Int](Right(5))
    val fromOption: OptionT[Either[String, *], Int] = OptionT.fromOption[Either[String, *]](Some(5))


    val test: OptionT[State[Int, *], String] = OptionT.liftF(State(s => (s + 1, s"Name $s")))
    val test2: OptionT[State[Int, *], String] = OptionT(State[Int, Option[String]](s => (s + 1, if (s % 5 == 0) None else Some(s"Name $s"))))

    val p = for {
      n1 <- test2
      n2 <- test2
      n3 <- test2
    } yield (n1, n2, n3)


    println(p.value.run(1).value)
  }
}
