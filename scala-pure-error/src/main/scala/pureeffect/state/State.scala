package pureeffect.state

case class State[S, +A](_run: S => (S, A)) { pa =>
  def map[B](f: A => B): State[S, B] = State { s0 =>
    val (s1: S, a: A) = pa.run(s0)
    val b: B = f(a)
    (s1, b)
  }

  def flatMap[B](f: A => State[S, B]): State[S, B] = State { s0 =>
    val (s1: S, a: A) = pa.run(s0)
    val pb: State[S, B] = f(a)
    val (s2: S, b: B) = pb.run(s1)
    (s2, b)
  }

  def mapState(f: S => S): State[S, A] = State { s0 =>
    val (s1: S, a: A) = pa.run(s0)
    val s2 = f(s1)
    (s2, a)
  }

  def contramapState(f: S => S): State[S, A] = State { s0 =>
    val s1 = f(s0)
    val (s2: S, a: A) = pa.run(s1)
    (s2, a)
  }

  def run(s: S): (S, A) = _run(s)
  def runState(s: S): S = run(s)._1
  def runResult(s: S): A = run(s)._2
}

object State {
  def success[A](a: A) : State[Unit, A] = State(s => (s, a))
  def get[S]: State[S, S] = State(s => (s, s))
  def set[S](s: S): State[S, Unit] = State(_ => (s, ()))
  def modify[S](f: S => S): State[S, Unit] = State(s => (f(s), ()))
  def update[S, A](_run: S => (S, A)): State[S, A] = State(_run)
}
