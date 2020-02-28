package pureerror.cats

import cats.Eval
import cats.implicits._
import cats.data.WriterT
import cats.kernel.Monoid

object WriterApp {
  type Writer[L, A] = WriterT[Eval, L, A]

  object Writer {
    def apply[L, V](l: L, v: V): WriterT[Eval, L, V] = WriterT[Eval, L, V](Eval.now((l, v)))
    def value[L: Monoid, V](v: V): Writer[L, V] = WriterT.value(v)
    def tell[L](l: L): Writer[L, Unit] = WriterT.tell(l)
  }

  def main(args: Array[String]): Unit = {
    val logAndFive: Writer[Vector[String], Int] = Writer(Vector("Returning 5"), 5)
    val tellHello: Writer[Vector[String], Unit] = Writer.tell(Vector("Hello"))
    val five: Writer[Vector[String], Int] = Writer.value(5)

    val program: Writer[Vector[String], Int] = for {
      n1 <- logAndFive
      _ <- tellHello
      n2 <- logAndFive
    } yield n1 + n2


    def countdown(n: Int): Writer[Vector[String], Unit] =
      if (n == 0)
        Writer.tell(Vector("Boom"))
      else
       for {
         _ <- Writer.tell(Vector(s"$n ..."))
         _ <- countdown(n - 1)
       } yield ()

    val value = program.value
    println(program.run.value)
    println(countdown(100).run.value)
  }
}
