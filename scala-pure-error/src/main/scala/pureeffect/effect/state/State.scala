package pureeffect.effect.state

case class State[S, +A] private (private val _run: S => (S, A)) { pa =>
  def run(s: S): (S, A) = _run(s)
  def runState(s: S): S = run(s)._1
  def runResult(s: S): A = run(s)._2

  def map[B](f: A => B): State[S, B] = State { s0 =>
    val (s1, a): (S, A) = pa.run(s0)
    val b: B = f(a)
    (s1, b)
  }

  def flatMap[B](f: A => State[S, B]): State[S, B] = State { s0 =>
    val (s1, a): (S, A) = pa.run(s0)
    val pb: State[S, B] = f(a)
    val (s2, b): (S, B) = pb.run(s1)
    (s2, b)
  }

  def mapState(f: S => S): State[S, A] = State { s0 =>
    val (s1, a): (S, A) = pa.run(s0)
    val s2: S = f(s1)
    (s2, a)
  }

  def contramapState(f: S => S): State[S, A] = State { s0 =>
    val s1: S = f(s0)
    val (s2, a): (S, A) = pa.run(s1)
    (s2, a)
  }
}

object State {
  def update[S, A](run: S => (S, A)): State[S, A] = State(run)

  def success[A](a: A) : State[Unit, A] = State.update(_ => ((), a))
  def get[S]: State[S, S] = State.update(s => (s, s))
  def set[S](s: S): State[S, Unit] = State.update(_ => (s, ()))
  def modify[S](f: S => S): State[S, Unit] = State.update(s => (f(s), ()))
}
