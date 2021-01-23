package pureeffect.indexedstate

case class IndexedState[-S1, +S2, +A](_run: S1 => (S2, A)) { pa =>
  def map[B](f: A => B): IndexedState[S1, S2, B] = IndexedState { s1 =>
    val (s2: S2, a: A) = pa.run(s1)
    val b: B = f(a)
    (s2, b)
  }

  def flatMap[S3, B](f: A => IndexedState[S2, S3, B]): IndexedState[S1, S3, B] = IndexedState { s1 =>
    val (s2: S2, a: A) = pa.run(s1)
    val pb: IndexedState[S2, S3, B] = f(a)
    val (s3: S3, b: B) = pb.run(s2)
    (s3, b)
  }

  def mapState[S3](f: S2 => S3): IndexedState[S1, S3, A] = IndexedState { s1 =>
    val (s2: S2, a: A) = pa.run(s1)
    val s3 = f(s2)
    (s3, a)
  }

  def contramapState[S0](f: S0 => S1): IndexedState[S0, S2, A] = IndexedState { s0 =>
    val s1 = f(s0)
    val (s2: S2, a: A) = pa.run(s1)
    (s2, a)
  }

  def run(s1: S1): (S2, A) = _run(s1)
  def runState(s: S1): S2 = run(s)._1
  def runResult(s: S1): A = run(s)._2
}

object IndexedState {
  def success[S, A](a: A) : IndexedState[S, S, A] = IndexedState(s => (s, a))
  def get[S]: IndexedState[S, S, S] = IndexedState(s => (s, s))
  def set[S](s: S): IndexedState[Any, S, Unit] = IndexedState(_ => (s, ()))
  def modify[S1, S2](f: S1 => S2): IndexedState[S1, S2, Unit] = IndexedState(s1 => (f(s1), ()))
  def update[S1, S2, A](_run: S1 => (S2, A)): IndexedState[S1, S2, A] = IndexedState(_run)
}
