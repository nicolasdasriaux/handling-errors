package pureerror.cats

import cats.Eval
import cats.data.ReaderT

object ReaderTApp {
  def main(args: Array[String]): Unit = {
    type Reader[R, A] = ReaderT[Eval, R, A]

    object Reader {
      def apply[A, B](f: A => B): Reader[A, B] = ReaderT[Eval, A, B](a => Eval.now(f(a)))
    }

    val unit = Reader[Int, String](_.toString)
    println(unit.run(6).value)
  }
}
