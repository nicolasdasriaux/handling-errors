package pureeffect.effect.reader

case class Reader[-R, +A] private (private val _run: R => A) { pa =>
  def run(r: R): A = _run(r)

  def map[B](f: A => B): Reader[R, B] = Reader { r =>
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

  def provideSome[RR](f: RR => R): Reader[RR, A] = Reader { rr =>
    val r: R = f(rr)
    val a: A = pa.run(r)
    a
  }

  def provide(r: R): Reader[Any, A] = Reader { _ =>
    pa.run(r)
  }
}

object Reader {
  def access[R, A](run: R => A): Reader[R, A] = Reader(run)

  def success[A](a: A): Reader[Any, A] = access(_ => a)
  def environment[R]: Reader[R, R] = access(r => r)
}
