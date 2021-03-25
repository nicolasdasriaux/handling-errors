package pureeffect.transformer.eithert

import cats.data.EitherT

object EitherTApp {
  def main(args: Array[String]): Unit = {
    val nested: Option[Either[String, Int]] = Some(Right(5))
    val apply: EitherT[Option, String, Int] = EitherT[Option, String, Int](nested)

    val pure = EitherT.pure[Option, String](5)
    val liftF = EitherT.liftF[Option, String, Int](Some(5))
    val fromEither = EitherT.fromEither[Option][String, Int](Right(5))

    val rightT = EitherT.rightT[Option, String](5)
    val right = EitherT.right[String][Option, Int](Some(5))
    val leftT = EitherT.leftT[Option, Int]("Error")
    val left = EitherT.left[Int][Option, String](Some("Error"))
  }
}
