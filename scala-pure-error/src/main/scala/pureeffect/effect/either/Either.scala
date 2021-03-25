package pureeffect.effect.either

sealed trait Either[+E1, +A] extends Product with Serializable { va =>
  import Either._

  def map[B](f: A => B): Either[E1, B] = va match {
    case Right(a) => Right(f(a))
    case Left(e1) => Left(e1)
  }

  def flatMap[E2 >: E1, B](f: A => Either[E2, B]): Either[E2, B] = va match {
    case Right(a) => f(a)
    case Left(e1) => Left(e1)
  }

  def mapError[E2](f: E1 => E2): Either[E2, A] = va match {
    case Right(a) => Right(a)
    case Left(e1) => Left(f(e1))
  }
}

object Either {
  final case class Right[+A](a: A) extends Either[Nothing, A]
  final case class Left[+E](e: E) extends Either[E, Nothing]

  def succeed[A](a: A): Either[Nothing, A] = Right(a)
  def fail[E](e: E): Either[E, Nothing] = Left(e)

  def apply[A](a: => A): Either[Throwable, A] =
    try {
      Either.succeed(a)
    } catch {
      case e: Throwable => Either.fail(e)
    }

  def fromOption[A](value: Option[A]): Either[Unit, A] = value match {
    case Some(a) => Either.succeed(a)
    case None => Either.fail(())
  }
}
