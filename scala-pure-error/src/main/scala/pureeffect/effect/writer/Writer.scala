package pureeffect.effect.writer

case class Writer[+W, +A] private (value: (List[W], A)) { va =>
  def map[B](f: A => B): Writer[W, B] = Writer {
    val (w, a): (List[W], A) = va.value
    val b: B = f(a)
    (w, b)
  }

  def flatMap[WW >: W, B](f: A => Writer[WW, B]): Writer[WW, B] = Writer {
    val (w1, a): (List[WW], A) = va.value
    val vb: Writer[WW, B] = f(a)
    val (w2, b): (List[WW], B) = vb.value
    (w1 ++ w2, b)
  }
}

object Writer {
  def success[W, A](a: A): Writer[W, A] = Writer(List.empty[W], a)
  def log[W](w: W): Writer[W, Unit] = Writer((List(w), ()))
}
