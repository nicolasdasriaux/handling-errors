package pureerror.eitherext

import scala.reflect.ClassTag
import scala.util.Try

object EitherExtension {
  implicit class EitherObjectExt(val self: Either.type) extends AnyVal {
    def succeed[A](a: A): Either[Nothing, A] = Right(a)
    def fail[E](e: E): Either[E, Nothing] = Left(e)
    def die(t: Throwable): Either[Nothing, Nothing] = throw t
    def dieMessage(message: String): Either[Nothing, Nothing] = die(new RuntimeException(message))
    def attempt[A](a: => A): Either[Throwable, A] = Try(a).toEither
  }

  implicit class EitherExt[E, A](val self: Either[E, A]) extends AnyVal {
    def mapError[E2](f: E => E2): Either[E2, A] = self.left.map(f)
    def flatMapError[E2](f: E => Either[E2, A]): Either[E2, A] = self.left.flatMap(f)
  }

  implicit class FailedEitherExt[E, A](val self: Either[E, A]) extends AnyVal {
    def orDie[E1 >: E](implicit ev: E1 <:< Throwable): Either[Nothing, A] =
      self.orDieWith(ev)

    def orDieWith(f: E => Throwable): Either[Nothing, A] =
      self.mapError { e => throw f(e) }

    def refineOrDie[E1](pf: PartialFunction[E, E1])(implicit ev: E <:< Throwable): Either[E1, A] =
      refineOrDieWith(pf)(ev)

    def refineOrDieWith[E1](pf: PartialFunction[E, E1])(f: E => Throwable): Either[E1, A] =
      self.mapError { e => pf.lift(e).getOrElse(throw f(e)) }

    def refineToOrDie[E1: ClassTag](implicit ev: E <:< Throwable): Either[E1, A] =
      refineOrDieWith({ case e: E1 => e })(ev)
  }
}
