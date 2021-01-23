package pureeffect.io

case class IO[+A](_unsafeRun: () => A) { pa =>
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

  def unsafeRun(): A = _unsafeRun()
}

object IO {
  def succeed[A](a: A): IO[A] = IO(() => a)
  def effectTotal[A](a: => A): IO[A] = IO(() => a)
}
