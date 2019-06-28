package reader

case class Reader[R, A](run: R => A) { ra =>
  def map[B](f: A => B): Reader[R, B] = Reader { r =>
    val a: A = ra.run(r)
    val b: B = f(a)
    b
  }

  def flatMap[B](f: A => Reader[R, B]): Reader[R, B] = Reader { r =>
    val a: A = ra.run(r)
    val rb: Reader[R, B] = f(a)
    val b: B = rb.run(r)
    b
  }

  def local[RR](f: RR => R): Reader[RR, A] = Reader { rr =>
    val r: R = f(rr)
    val a: A = ra.run(r)
    a
  }
}
