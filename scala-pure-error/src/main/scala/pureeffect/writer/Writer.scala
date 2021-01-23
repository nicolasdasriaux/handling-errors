package pureeffect.writer

case class Writer[+W, +A](_run: (List[W], A)) { pa =>
  def map[B](f: A => B): Writer[W, B] = Writer {
    val (w, a) = pa.run
    val b = f(a)
    (w, b)
  }

  def flatMap[WW >: W, B](f: A => Writer[WW, B]): Writer[WW, B] = Writer {
    val (w1, a) = pa.run
    val pb = f(a)
    val (w2, b) = pb.run
    (w1 ++ w2, b)
  }

  def run: (List[W], A) = _run
}

object Writer {
  def success[W, A](a: A): Writer[W, A] = Writer(List.empty[W], a)
  def log[W](w: W): Writer[W, Unit] = Writer((List(w), ()))
}
