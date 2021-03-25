package pureeffect.effect.io

import pureeffect.effect.either.Either

case class IO[+A] private (private val _unsafeRun: () => A) { pa =>
  def unsafeRun(): A = _unsafeRun()

  def map[B](f: A => B): IO[B] = IO { () =>
    val a: A = pa.unsafeRun()
    val b: B = f(a)
    b
  }

  def flatMap[B](f: A => IO[B]): IO[B] = IO { () =>
    val a: A = pa.unsafeRun()
    val pb: IO[B] = f(a)
    val b: B = pb.unsafeRun()
    b
  }

  def attempt: IO[Either[Throwable, A]] = IO { () =>
    try Either.succeed(pa.unsafeRun())
    catch {
      case ex: Throwable => Either.fail(ex)
    }
  }
}

object IO {
  def succeed[A](a: A): IO[A] = IO(() => a)
  def effectTotal[A](a: => A): IO[A] = IO(() => a)
}
