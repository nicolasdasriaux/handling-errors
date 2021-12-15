package pureerror.validation

import pureeffect.effect.either.Either

trait Validation[+E1, +A] extends Product with Serializable { va =>
  import Validation._

  def map[B](f: A => B): Validation[E1, B] = va match {
    case Success(a) => Success(f(a))
    case Failure(e1) => Failure(e1)
  }

  def flatMap[E2 >: E1, B](f: A => Validation[E2, B]): Validation[E2, B] = va match {
    case Success(a) => f(a)
    case Failure(e1) => Failure(e1)
  }

  def zipPar[B, E2 >: E1](vb: Validation[E2, B]): Validation[E2, (A, B)] = (va, vb) match {
    case (Success(a), Success(b)) => Success((a, b))
    case (Failure(e1), Success(_)) => Failure(e1)
    case (Success(_), Failure(e2)) => Failure(e2)
    case (Failure(e1), Failure(e2)) => Failure(e1 ++ e2)
  }

  def <&>[B, E2 >: E1](vb: Validation[E2, B]): Validation[E2, (A, B)] = va.zipPar(vb)
}

object Validation {
  import Either._

  final case class Success[+A](result: A) extends Validation[Nothing, A]
  final case class Failure[+E](errors: List[E]) extends Validation[E, Nothing]

  def succeed[A](result: A): Validation[Nothing, A] = Success(result)
  def fail[E](error: E): Validation[E, Nothing] = Failure(List(error))

  def fromOption[A](option: Option[A]): Validation[Unit, A] = option match {
    case Some(a) => succeed(a)
    case None => fail(())
  }

  def fromEither[E, A](either: Either[E, A]): Validation[E, A] = either match {
    case Right(a) => succeed(a)
    case Left(e) => fail(e)
  }

  def validateWith[E, A0, A1, B](
    a0: Validation[E, A0],
    a1: Validation[E, A1]
  )(
    f: (A0, A1) => B
  ): Validation[E, B] =
    (a0 <&> a1).map({ case (a, b) => f(a, b) })

  def validateWith[E, A0, A1, A2, B](
    a0: Validation[E, A0],
    a1: Validation[E, A1],
    a2: Validation[E, A2]
  )(
    f: (A0, A1, A2) => B
  ): Validation[E, B] =
    (a0 <&> a1 <&> a2).map({ case ((a0, a1), a2) => f(a0, a1, a2) })

  def validateWith[E, A0, A1, A2, A3, B](
    a0: Validation[E, A0],
    a1: Validation[E, A1],
    a2: Validation[E, A2],
    a3: Validation[E, A3]
  )(
    f: (A0, A1, A2, A3) => B
  ): Validation[E, B] =
    (a0 <&> a1 <&> a2 <&> a3).map({ case (((a0, a1), a2), a3) => f(a0, a1, a2, a3) })

  def validateWith[E, A0, A1, A2, A3, A4, B](
    a0: Validation[E, A0],
    a1: Validation[E, A1],
    a2: Validation[E, A2],
    a3: Validation[E, A3],
    a4: Validation[E, A4]
  )(
    f: (A0, A1, A2, A3, A4) => B
  ): Validation[E, B] =
    (a0 <&> a1 <&> a2 <&> a3 <&> a4).map({ case ((((a0, a1), a2), a3), a4) => f(a0, a1, a2, a3, a4) })

  def validate[E, A0, A1](
    a0: Validation[E, A0],
    a1: Validation[E, A1]
  ): Validation[E, (A0, A1)] =
    validateWith(a0, a1)((_, _)) // (_, _) equivalent to (a0, a1) => (a0, a1)

  def validate[E, A0, A1, A2](
    a0: Validation[E, A0],
    a1: Validation[E, A1],
    a2: Validation[E, A2]
  ): Validation[E, (A0, A1, A2)] =
    validateWith(a0, a1, a2)((_, _, _))

  def validate[E, A0, A1, A2, A3](
    a0: Validation[E, A0],
    a1: Validation[E, A1],
    a2: Validation[E, A2],
    a3: Validation[E, A3]
  ): Validation[E, (A0, A1, A2, A3)] =
    validateWith(a0, a1, a2, a3)((_, _, _, _))

  def validate[E, A0, A1, A2, A3, A4, B](
    a0: Validation[E, A0],
    a1: Validation[E, A1],
    a2: Validation[E, A2],
    a3: Validation[E, A3],
    a4: Validation[E, A4]
  ): Validation[E, (A0, A1, A2, A3, A4)] =
    validateWith(a0, a1, a2, a3, a4)((_, _, _, _,_))
}
