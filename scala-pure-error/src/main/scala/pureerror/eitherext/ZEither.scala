package pureerror.eitherext

import zio.{CanFail, ZIO}
import zio.prelude.Validation
import zio.prelude.fx.ZPure
import zio.prelude.fx.ZPure.fail

import scala.reflect.ClassTag
import scala.util.Try


object ZEither {
  implicit class EitherCompanionExt(val self: Either.type) extends AnyVal {
    def succeed[A](a: A): Either[Nothing, A] = Right(a)
    def fail[E](e: E): Either[E, Nothing] = Left(e)
    def attempt[A](a: => A): Either[Throwable, A] = Try(a).toEither

    def die(t: Throwable): Nothing = throw t // IMPURE
    def dieMessage(message: String): Nothing = die(new RuntimeException(message)) // IMPURE
  }

  implicit class EitherExt[E, A](val self: Either[E, A]) extends AnyVal {
    def toValidation: Validation[E, A] = Validation.fromEither(self)
  }

  implicit class FailedEitherExt[E, A](val self: Either[E, A]) extends AnyVal {
    def mapError[E2](f: E => E2): Either[E2, A] = self.left.map(f)
    def flatMapError[E2](f: E => Either[E2, A]): Either[E2, A] = self.left.flatMap(f)

    def orDie[E1 >: E](implicit ev: E1 <:< Throwable): Either[Nothing, A] =
      orDieWith(ev)

    def orDieWith(f: E => Throwable): Either[Nothing, A] =
      mapError { e => throw f(e) }

    def refineOrDie[E1](pf: PartialFunction[E, E1])(implicit ev: E <:< Throwable): Either[E1, A] =
      refineOrDieWith(pf)(ev)

    def refineOrDieWith[E1](pf: PartialFunction[E, E1])(f: E => Throwable): Either[E1, A] =
      mapError { e => pf.lift(e).getOrElse(throw f(e)) }

    def refineToOrDie[E1: ClassTag](implicit ev: E <:< Throwable): Either[E1, A] =
      refineOrDieWith({ case e: E1 => e })(ev)

    def catchAll[E2, A1 >: A](h: E => Either[E2, A1]):  Either[E2, A1] =
      foldM(h, Either.succeed)

    def catchSome[E1 >: E, A1 >: A](
                                     pf: PartialFunction[E, Either[E1, A1]]
                                   ): Either[E1, A1] =
      catchAll(e => pf.applyOrElse[E, Either[E1, A1]](e, Either.fail))

    def foldM[E2, B](failure: E => Either[E2, B], success: A => Either[E2, B]): Either[E2, B] = self.fold(failure, success)
    def _filterOrElse[E2 >: E, B >: A](p: A => Boolean)(f: A => Either[E2, B]): Either[E2, B] = self.foldM(Either.fail, a => if (p(a)) Either.succeed(a) else f(a))
  }
}
