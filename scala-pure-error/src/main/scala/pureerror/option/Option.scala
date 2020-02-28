package pureerror.option

sealed trait Option[+A] extends Product with Serializable {
  def map[B](f: A => B): Option[B] = this match {
    case None => None

    case Some(a) =>
      val b = f(a)
      Some(b)
  }

  def flatMap[B](f: A => Option[B]): Option[B] = this match {
    case None => None

    case Some(a) =>
      val maybeB = f(a)
      maybeB
  }
}

case object None extends Option[Nothing]
case class Some[A](a: A) extends Option[A]
