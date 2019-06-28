package either

sealed trait Either[+E1, +A] extends Product with Serializable {
  def map[B](f: A => B): Either[E1, B] = this match {
    case Right(a) => Right(f(a))
    case Left(e1) => Left(e1)
  }

  def flatMap[E2 >: E1, B](f: A => Either[E2, B]): Either[E2, B] = this match  {
    case Right(a) => f(a)
    case Left(e1) => Left(e1)
  }

  def leftMap[E2](f: E1 => E2): Either[E2, A] = this match {
    case Right(a) => Right(a)
    case Left(e1) => Left(f(e1))
  }

  def leftFlatMap[E2, B >: A](f: E1 => Either[E2, B]): Either[E2, B] = this match {
    case Right(a) => Right(a)
    case Left(e1) => f(e1)
  }

  def ensure[E2 >: E1](onFailure: => E2)(p: A => Boolean): Either[E2, A] = this match {
    case Right(a) => if (p(a)) Right(a) else this
    case Left(e1) => Left(e1)
  }

  def foreach(f: A => Unit): Unit = this match {
    case Right(a) => f(a)
    case Left(_) => ()
  }
}

object Either {
  def right[E1, A](a: A): Either[E1, A] = Right(a)
  def left[E1, A](e1: E1): Either[E1, A] = Left(e1)

  def cond[E1, A](test: Boolean, right: => A, left: => E1): Either[E1, A] =
    if (test) Right(right) else Left(left)

  implicit class EitherIdOps[A](val self: A) extends AnyVal {
    def asLeft[B]: Either[A, B] = Left(self)
    def asRight[B]: Either[B, A] = Right(self)
  }

  object EitherSyntax {
  }
}

final case class Left[+E](e: E) extends Either[E, Nothing]
final case class Right[+A](a: A) extends Either[Nothing, A]
