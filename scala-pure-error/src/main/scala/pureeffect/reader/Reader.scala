package pureeffect.reader

case class Reader[-R, +A](_run: R => A) { pa =>
  def map[B](f: A => B): Reader[R, B] = Reader.access { r =>
    val a: A = pa.run(r)
    val b: B = f(a)
    b
  }

  def flatMap[B, RR <: R](f: A => Reader[RR, B]): Reader[RR, B] = Reader { rr =>
    val a: A = pa.run(rr)
    val rb: Reader[RR, B] = f(a)
    val b: B = rb.run(rr)
    b
  }

  /**
   * contramapEnvironment
   */
  def provideSome[RR](f: RR => R): Reader[RR, A] = Reader { rr =>
    val r: R = f(rr)
    val a: A = pa.run(r)
    a
  }

  def provide(r: R): Reader[Any, A] = Reader { _ =>
    pa.run(r)
  }

  def run(r: R): A = _run(r)
}

object Reader {
  def success[A](a: A): Reader[Any, A] = Reader(_ => a)
  def environment[R]: Reader[R, R] = Reader(r => r)
  def access[R, A](_run: R => A): Reader[R, A] = Reader(_run)
}
