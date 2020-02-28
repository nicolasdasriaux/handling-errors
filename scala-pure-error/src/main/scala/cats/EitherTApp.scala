package cats

import cats.implicits._
import cats.data.EitherT

object EitherTApp {
  def main(args: Array[String]): Unit = {
    val t12 = EitherT.pure[Option, String](5)
    val t3 = EitherT.liftF[Option, String, Int](5.some)
    val t4 = EitherT.fromEither[Option](3.asRight[String])
    val t5 = EitherT(1.asRight[String].some)

    val t1 = EitherT.rightT[Option, String](5)
    val t2 = EitherT.right[String](3.some)
  }
}
